# GIIS Editor

A 2D/3D graphics editor built in Java 21 featuring implementations of classic computer graphics algorithms.

## Features

### Drawing Tools

- **Pen** - freehand drawing
- **Lines** - line segments with algorithm selection (Bresenham, CDA, Straight Line)
- **Antialiasing** - line smoothing

### Curves

- **Circle** - by center and radius
- **Ellipse** - by two foci
- **Parabola** - by vertex and point
- **Hyperbola** - by foci

### Polygons

- **Arbitrary Polygon** - by set of points
- **Triangle** - isosceles, right-angle
- **Quadrilaterals** - rectangle
- **Regular n-gon** - with specified number of vertices
![Shapes](images/shapes.png)
### Fill Algorithms

- **Simple Seed Fill** - classical seed fill
- **Scanline Seed Fill** - scanline seed fill algorithm
- **Scanline AEL/OEL Fill** - active edge list algorithms

![Fill Menu](images/fill_menu.png)

![Filling Result](images/filling.png)

### Convex Hull

- **Graham Scan** - Graham scan algorithm
- **Jarvis March** - Jarvis march algorithm

### Triangulation

- **Delaunay** - Delaunay triangulation
- **Voronoi** - Voronoi diagram

![Delaunay Base](images/delone_base.png)

![Delaunay Result](images/delone_result.png)

![Voronoi Result](images/voronoi_result.png)

### 3D Modeling

- Load `.obj` format models
- Perspective projection
- Transformations: rotation, scaling, translation
- Face and edge rendering

![3D Example 1](images/3d_example.png)

![3D Example 2](images/3d_example2.png)

![3D Example 3](images/3d_example3.png)

### Files

- Save/load scenes in `.giis` format
- Export 3D models to `.obj`

### Debug Mode

- Step-by-step algorithm execution

### Morph Mode
- Сhanging the shape of figures based on anchor points 
![Morph Step 1](images/morph1.png)

![Morph Step 2](images/morph2.png)

![Morph Step 3](images/morph3.png)

## Build & Run

```bash
./mvnw clean install   # build
./mvnw exec:java     # run
```

## Keyboard Shortcuts

Use the toolbars to select modes and tools.