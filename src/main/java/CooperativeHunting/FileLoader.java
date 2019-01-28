package CooperativeHunting;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

class FileLoader {

    static void loadFromFile(String dialogTitle, String handleMethodName, GUI gui) {
        // open file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(dialogTitle);
        File file = fileChooser.showOpenDialog(gui.stage);

        // can create file
        if (file == null) return;

        // read file content
        String content = FileLoader.readFile(file);

        // call method
        try {
            Class[] paramTypes = new Class[]{String.class, GUI.class};
            Object[] param = new Object[]{content, gui};
            Method method = FileLoader.class.getDeclaredMethod(handleMethodName, paramTypes);
            method.invoke(null, param);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            GUI.alert("Error", "Error deserialize data");
        }
    }

    static void saveToFile(String dialogTitle, String handleMethodName, GUI gui) {
        // create new file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(dialogTitle);
        File file = fileChooser.showSaveDialog(gui.stage);

        // can create file
        if (file == null) return;

        // call method
        try {
            Class[] paramTypes = new Class[]{GUI.class};
            Object[] param = new Object[]{gui};
            Method method = FileLoader.class.getDeclaredMethod(handleMethodName, paramTypes);
            String content = (String) method.invoke(null, param);
            // write to file
            FileLoader.writeFile(file, content);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            GUI.alert("Error", "Error serialize data");
        }
    }

    static void loadSettingsFromString(String data, GUI gui) {
        if (data == null) {
            GUI.alert("Error", "Load settings fails");
            return;
        }

        data = data.replaceAll("\r", "");

        try {
            String[] values = data.split("\n");
            int i = 0;

            for (int j = 0; j < gui.inputTextFields.length; j++, i++)
                gui.inputTextFields[j].setText(values[i]);
            for (int j = 0; j < gui.checkBoxes.length; j++, i++)
                gui.checkBoxes[j].setSelected(Boolean.parseBoolean(values[i]));
            for (int j = 0; j < gui.colorPickers.length; j++, i++)
                gui.colorPickers[j].setValue(Color.valueOf(values[i]));

            gui.apply();
            gui.map.clear();
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            GUI.alert("Warning", "Corrupted default value file");
        } catch (IllegalArgumentException e) {
            GUI.alert("Warning", "Corrupted color value in default value file");
        }
    }

    static void loadMapFromString(String data, GUI gui) {
        if (data == null) {
            GUI.alert("Error", "Corrupted map data");
            return;
        }

        data = data.replaceAll("\r", "");

        // check map size
        String[] lines = data.split("\n");
        if (lines.length < 2) {
            GUI.alert("Error", "Corrupted map data");
            return;
        }
        int height, width;
        try {
            height = Integer.parseInt(lines[0].split(" ")[0]);
            width = Integer.parseInt(lines[0].split(" ")[1]);
        } catch (NumberFormatException e) {
            GUI.alert("Error", "Corrupted map data");
            return;
        }

        // check data row
        if (lines.length != height + 1) {
            GUI.alert("Error", "Corrupted map data");
            return;
        }

        // check data column
        for (int i = 1; i <= height; i++)
            if (lines[i].length() != width) {
                GUI.alert("Error", "Corrupted map data");
                return;
            }

        // check map size compatibility
        if (gui.map.getMapWidth() != width || gui.map.getMapHeight() != height) {
            GUI.alert("Error", "Map size differs from current settings");
            return;
        }

        // clear all old data in map
        Map map = gui.map;
        List<Predator> predators = map.getPredators();
        List<Prey> preys = map.getPreys();
        map.clear();

        // populate map
        for (int i = 1; i <= map.getMapHeight(); i++) {
            for (int j = 0; j < map.getMapWidth(); j++) {
                char c = lines[i].charAt(j);
                // predator is marked as x
                if (c == 'x')
                    predators.add(new Predator(new Position(j, i - 1)));
                else {
                    // prey is marked with its size (0 < size < 10)
                    int size = "123456789".indexOf(c) + 1;
                    if (size > 0)
                        preys.add(new Prey(new Position(j, i - 1), size));
                    // other char -> empty
                }
            }
        }

        gui.enablePlayButton();
        map.paint();
    }

    static void loadSimulationFromString(String data, GUI gui) {
        Map map;
        String settingsString;
        try {
            byte[] dataByte = Base64.getDecoder().decode(data);
            ByteArrayInputStream bais = new ByteArrayInputStream(dataByte);
            ObjectInputStream stream = new ObjectInputStream(bais);
            map = (Map) stream.readObject();
            settingsString = (String) stream.readObject();
            stream.close();
            bais.close();
        } catch (IOException | ClassNotFoundException e) {
            GUI.alert("Error", "Can't deserialize simulation");
            return;
        }

        loadSettingsFromString(settingsString, gui);
        gui.setMap(map);
        map.setController(gui);
        gui.enablePlayButton();

        for (Prey prey : map.getPreys())
            prey.postDeserialize();
        for (Predator predator : map.getPredators())
            predator.postDeserialize();

        map.paint();
    }

    static String saveSettingsToString(GUI gui) {
        StringBuilder content = new StringBuilder();
        for (TextField textField : gui.inputTextFields)
            content.append(textField.getText()).append('\n');
        for (CheckBox checkBox : gui.checkBoxes)
            content.append(checkBox.isSelected() ? "true" : "false").append('\n');
        for (ColorPicker colorPicker : gui.colorPickers)
            content.append(colorPicker.getValue().toString()).append('\n');
        return content.toString();
    }

    static String saveMapToString(GUI gui) {
        Map map = gui.map;
        char[][] data = new char[map.getMapHeight()][map.getMapWidth()];

        // empty map
        for (char[] line : data)
            Arrays.fill(line, '.');

        // populate data
        for (Predator predator : map.getPredators())
            data[predator.y][predator.x] = 'x';
        for (Prey prey : map.getPreys())
            data[prey.y][prey.x] = "123456789".charAt(prey.size - 1);

        // convert data array to string
        StringBuilder builder = new StringBuilder();
        builder.append(map.getMapHeight()).append(' ')
                .append(map.getMapWidth()).append('\n');
        for (char[] line : data)
            builder.append(line).append('\n');
        return builder.toString();
    }

    static String saveSimulationToString(GUI gui) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream stream = new ObjectOutputStream(baos);
            stream.writeObject(gui.map);
            stream.writeObject(saveSettingsToString(gui));
            String result = Base64.getEncoder().encodeToString(baos.toByteArray());
            stream.close();
            baos.close();
            return result;
        } catch (IOException e) {
            GUI.alert("Error", "Can serialize simulation");
            return null;
        }
    }

    /**
     * @param fileName: file to read
     * @return file content string
     */
    static String readResourceFile(String fileName) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream stream = classloader.getResourceAsStream(fileName);
        return readFile(stream);
    }

    private static String readFile(File file) {
        try {
            InputStream stream = new FileInputStream(file);
            return readFile(stream);
        } catch (IOException e) {
            return null;
        }
    }

    private static void writeFile(File file, String content) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            GUI.alert("Error", "Error while saving file '" + file.getName() + "':\n" + e.getMessage());
        }
    }

    private static String readFile(InputStream stream) {
        try {
            if (stream == null)
                throw new FileNotFoundException();
            Scanner s = new Scanner(stream).useDelimiter("\\A");
            String content = s.hasNext() ? s.next() : "";
            stream.close();
            return content;
        } catch (IOException e) {
            return null;
        }
    }
}
