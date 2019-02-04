SIMULATING AND VISUALIZING COOPERATIVE HUNTING
==============================================

## 1. INTRODUCTION

Every living form needs some kind of nutrition, and while some obtain it through "peaceful"
ways, e.g. photosynthesis or from plants, others have adapted to more feral techniques. One
such example is hunting, which is the process of pursuing and killing other live forms. The
rich diversity of animals that have evolved over millions of years has also introduced a rich
diversity of hunting techniques, each with its pros and cons. An especially interesting strategy is
the cooperative hunting, which is related to the evolution of sociability between animals. It
is a phenomenon where individuals work/hunt together to capture more prey at a lower over-
all risk/cost. Moreover, solitary hunting may even be impossible, effectively forcing predators
to cooperate in order to survive. Lions, wolves and humans are typical examples. Cooperative
hunting is a complex behaviour as it requires social skills and coordination within the hunting
party. For instance often the prey is much faster than the predators, who must employ
cornering and surrounding approaches to counter this advantage. In other cases the prey is
simply too strong for the individual predator, and thus a larger group is needed to bring it
down.

A predator would generally stick to a single hunting strategy. Some, however, are versatile
and adapt to the circumstances. After all, what counts in the end is surviving. Therefore, a
generally solitary hunter may cooperate with others to kill prey otherwise too strong for him
in order to survive, and a group hunter may see the opportunity of an easy solitary hunt and
not inform the group, thus keeping everything for himself.


## 2. Project scope

The goal of this project is to simulate and visualize the various hunting strategies as well as
manoeuvres used by predators to corner and surround their prey. For this purpose an application
has to be programmed in Java consisting of an animation window displaying the simulation's state,
a statistics window displaying information about the simulation (see 2.2),
as well as at window containing the graphical user interface to operate the simulation and
feed it the input parameters (see 2.1). The animation can be a simple 2D Cellular automaton
l;3l. where the environment is represented by a grid and each object in it by a Coloured position or
group of tiles (see Fig. 2.1). The application should be able to display the moving predator
and prey individuals as well as the predator group radius (see Fig. 2.2). Changes in the
environment are done by updating the respective tiles’ colour, e. g. if a predator moves from position
A to position 8, then position B will be changed to the predator's colour, whereas position A will receive the
colour representing an empty position. You should implement the following rules regarding the hunt:

1. Each Predator individual must be able to make a decision regarding his hunting strategy
based on the given circumstances (hunt small prey alone or notify group when help
for larger prey is needed). Other hunting strategies apart from cooperative and solitary
are not needed.

2. When hunting large prey in a group the predators must consider and stay within the
group radius. while still trying to corner and surround the prey.

3. A prey is considered killed only if a predator within a group of the needed size reaches it.

4. If the predator group size is not of sufficient size the prey will retaliate by attacking
the closest predator and killing it should it score a number higher than the predator's defence.

5. Predators being attacked by the prey should try to escape and join the group again (be
within the group radius).

6. The predators may employ various manoeuvre tactics to corner the prey. without it
being aware of them, i.e. the prey should simply try to get as far as possible from the
predators without any specific strategy in mind.

7. Predators should be able to see prey and other predators from the group only within
some radius (preferably greater than the group radius). Outside of this radius a predator may signal its presences and location to the others by "howling".

8. Each iteration all predators lose hit points due to starvation. These can be regained
only through killing prey.

### 2.1 Input

The input parameters for the application include. but are not limited to:

- **Grid size** - the width and the height of the grid (in number of tiles as in Fig. 2.1).

- **Initial predator count** - the number of predators at the beginning of the simulation.

- **Prey size/position** - position of a prey animal on the grid for instance as a pair of x, y
coordinates and its size in grid tiles (small prey is easier to kill, but gives less nutrition).

- **Auto-generate prey** - when activated this option should enable the application to generate
prey animals at random locations every x seconds.

- **Predator run speed** - how many tiles can the predator cross in a single iteration.

- **Prey run speed** - how many tiles can the prey cross in a single iteration.

- **Predator starvation resilience** - how many iterations can a predator individual endure
without food intake (the predator dies if he does not successfully hunt a prey during
this period).

- **Predator defence chance** - a larger prey would attack a lone predator and kill it if it
scores a number greater than the predator's defence.

- **Predator group radius** - if a number of predator individuals are within a given distance
of each other, they form a group and can hunt larger prey.

- **Simulation object colours** - for instance a drop down menu with several colour options
for the predator objects and for the various prey objects (no duplicates).

- **Simulation speed** - the overall speed of the simulation/animation (a lower speed may
be desirable when observing the program in action, whereas a higher speed can deliver
results more quickly).

### 2.2 Output

The outputs delivered by the application should include, but are not limited to:

- Average food gain per iteration - the average nutrition from killed prey per iteration
that the predator population has gained.

- Predator count - remaining predator individuals.

## 3 Possible pitfalls

- Effective scouting - if the group always stays together it will be stronger, but will find
less prey. Therefore, a strategy is needed to disperse the group and bring it back together
when prey is found.

- Hunting in subgroups - a dispersed group may locate various prey animals in different
locations. lt should be able to hunt in subgroups in order to kill as much of the prey as
possible, instead of bringing the entire group back for a single hunt.