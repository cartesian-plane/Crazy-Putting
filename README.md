# Crazy Golf! - Advanced Physics Simulation & AI Project

This repository contains the source code for "Crazy Putting!", a university project developed for KEN1600 Project 1.2 at Maastricht University (Bachelor Data Science and Artificial Intelligence, Academic Year 2023-2024). The project focuses on creating a comprehensive physics simulator for a golf putting game, featuring advanced AI agents, complex terrain handling, and a modular design.

![image](https://github.com/user-attachments/assets/675fb34b-09a3-405f-ad37-e7bb8360695f)

## How to Run:
The project comes with a runnable JAR located in the root of the repository. To run the game, copy the following 
commands into your terminal (starting from the root of the repository):

```bash
cd assets
java -jar ../crazy-putting-1.0.jar
```

## Project Overview

The core objective was to build a simulator capable of modeling golf ball motion on varying terrains with obstacles, governed by differential equations. Key aspects include:

* **A general-purpose physics engine:** Designed for flexibility, allowing different physical laws and numerical integration methods to be implemented and swapped.
* **Intelligent AI agents:** Development of AI bots capable of analyzing the course and determining optimal shots using various optimization algorithms.
* **Complex environment simulation:** Handling terrains defined by mathematical functions and incorporating various types of obstacles.
* **User interaction:** Providing tools for users to create, modify, and visualize golf courses.

## Features

* **Modular Physics Engine:**
    * Decoupled implementation of physical laws (gravity, friction) and Ordinary Differential Equation (ODE) solvers.
    * Supports multiple numerical integrators like Euler, Runge-Kutta 4 (RK4), and Verlet methods for simulating ball physics.
* **Dynamic Terrain Generation:**
    * Input terrains defined by mathematical equations `h(x, y)`.
    * Includes a custom parser (using Visitor pattern) and a highly optimized runtime compilation approach to evaluate terrain height and gradients efficiently, overcoming initial performance bottlenecks with lambda expressions.
* **Obstacle Simulation:**
    * Integration of various static obstacles like trees, walls, sand pits, and water bodies.
    * Sophisticated collision detection using convex polygon checks based on cross-product logic.
    * Realistic physics responses upon collision (e.g., velocity reflection).
* **Interactive Level Editor:**
    * A graphical user interface (GUI) allowing users to visually design, create, and modify golf course layouts, including terrain and obstacle placement.
* **AI Player Agents:**
    * Implementation of multiple AI strategies:
        * Hill-Climbing (Line Search)
        * Newton-Raphson Method
        * Simulated Annealing
    * AI agents map input actions (shot angle and velocity) to predicted final ball positions.
    * Utilizes A* pathfinding algorithm with custom heuristics for navigation and optimization, especially effective on complex, maze-like courses.
* **Graphical User Interface:**
    * Built using the [LibGDX] library.
    * Includes menus for simulation settings (solver choice, step size), level selection, course editing, and bot configuration.

## Technical Details

* **Language:** Java
* **Core Concepts:**
    * Numerical Methods (ODE Solvers)
    * Physics Simulation & Modeling
    * Artificial Intelligence (Search Algorithms, Optimization)
    * Collision Detection Algorithms
    * Design Patterns (Visitor)
    * Data Structures & Algorithms
    * Runtime Code Generation/Compilation
    * Software Engineering Principles (Modularity, Testing)
* **Libraries:** [LibGDX](https://libgdx.com/) for graphics and UI.

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.aa
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/lib`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.
