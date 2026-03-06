package com.bsuir.giis.editor.utils;

import com.bsuir.giis.editor.model.Point;

/**
 * Универсальный утилитный класс для выполнения матричных вычислений.
 * Поддерживает матрицы произвольного размера.
 */
public class MatrixUtils {

    private static final double EPSILON = 1e-10;


    /**
     * Создает нулевую матрицу заданного размера
     */
    public static double[][] createMatrix(int rows, int cols) {
        return new double[rows][cols];
    }

    /**
     * Создает единичную матрицу размера n×n
     */
    public static double[][] identityMatrix(int n) {
        double[][] matrix = createMatrix(n, n);
        for (int i = 0; i < n; i++) {
            matrix[i][i] = 1.0;
        }
        return matrix;
    }

    /**
     * Создает матрицу из массива значений
     */
    public static double[][] createMatrix(double[][] values) {
        int rows = values.length;
        int cols = values[0].length;
        double[][] matrix = createMatrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            System.arraycopy(values[i], 0, matrix[i], 0, cols);
        }
        return matrix;
    }


    /**
     * Вычисление коэффициентов для кривой Эрмита
     * @param matrix базисная матрица 4x4
     * @param p0 начальная точка
     * @param p1 конечная точка
     * @param v0x касательная в начале (x)
     * @param v0y касательная в начале (y)
     * @param v1x касательная в конце (x)
     * @param v1y касательная в конце (y)
     * @return матрица коэффициентов 4x2
     */
    public static double[][] multiplyHermiteGeometry(double[][] matrix,
                                                     Point p0, Point p1,
                                                     double v0x, double v0y,
                                                     double v1x, double v1y) {
        if (matrix == null || matrix.length != 4) {
            throw new IllegalArgumentException("Matrix must be 4x4");
        }

        double[][] result = createMatrix(4, 2);
        double[] geometryX = { p0.getX(), p1.getX(), v0x, v1x };
        double[] geometryY = { p0.getY(), p1.getY(), v0y, v1y };

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i][0] += matrix[i][j] * geometryX[j];
                result[i][1] += matrix[i][j] * geometryY[j];
            }
        }
        return result;
    }
    /**
     * Сложение двух матриц
     */
    public static double[][] add(double[][] a, double[][] b) {
        validateSameDimensions(a, b);
        int rows = a.length;
        int cols = a[0].length;
        double[][] result = createMatrix(rows, cols);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = a[i][j] + b[i][j];
            }
        }
        return result;
    }

    /**
     * Вычитание двух матриц
     */
    public static double[][] subtract(double[][] a, double[][] b) {
        validateSameDimensions(a, b);
        int rows = a.length;
        int cols = a[0].length;
        double[][] result = createMatrix(rows, cols);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = a[i][j] - b[i][j];
            }
        }
        return result;
    }

    /**
     * Умножение матрицы на скаляр
     */
    public static double[][] multiplyByScalar(double[][] matrix, double scalar) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[][] result = createMatrix(rows, cols);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = matrix[i][j] * scalar;
            }
        }
        return result;
    }

    /**
     * Умножение двух матриц (A × B)
     */
    public static double[][] multiply(double[][] a, double[][] b) {
        if (a == null || b == null || a[0].length != b.length) {
            throw new IllegalArgumentException(
                    String.format("Invalid matrix dimensions: %dx%d and %dx%d",
                            a != null ? a.length : 0, a != null && a.length > 0 ? a[0].length : 0,
                            b != null ? b.length : 0, b != null && b.length > 0 ? b[0].length : 0)
            );
        }

        int rowsA = a.length;
        int colsA = a[0].length;
        int colsB = b[0].length;
        double[][] result = createMatrix(rowsA, colsB);

        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                for (int k = 0; k < colsA; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return result;
    }

    /**
     * Умножение матрицы на вектор
     */
    public static double[] multiplyVector(double[][] matrix, double[] vector) {
        if (matrix == null || vector == null || matrix[0].length != vector.length) {
            throw new IllegalArgumentException("Invalid matrix/vector dimensions");
        }

        int rows = matrix.length;
        double[] result = new double[rows];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < vector.length; j++) {
                result[i] += matrix[i][j] * vector[j];
            }
        }
        return result;
    }


    /**
     * Умножение матрицы на массив контрольных точек
     * Возвращает матрицу коэффициентов [rows][2] для x и y
     */
    public static double[][] multiplyPoints(double[][] matrix, Point[] controlPoints) {
        if (matrix == null || controlPoints == null || matrix[0].length != controlPoints.length) {
            throw new IllegalArgumentException("Invalid matrix/points dimensions");
        }

        int rows = matrix.length;
        double[][] result = createMatrix(rows, 2);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < controlPoints.length; j++) {
                result[i][0] += matrix[i][j] * controlPoints[j].getX();
                result[i][1] += matrix[i][j] * controlPoints[j].getY();
            }
        }
        return result;
    }

    /**
     * Транспонирование матрицы
     */
    public static double[][] transpose(double[][] matrix) {
        if (matrix == null || matrix.length == 0) {
            throw new IllegalArgumentException("Matrix cannot be null or empty");
        }

        int rows = matrix.length;
        int cols = matrix[0].length;
        double[][] result = createMatrix(cols, rows);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[j][i] = matrix[i][j];
            }
        }
        return result;
    }

    /**
     * Вычисление определителя матрицы (только для квадратных)
     */
    public static double determinant(double[][] matrix) {
        if (!isSquare(matrix)) {
            throw new IllegalArgumentException("Matrix must be square");
        }

        int n = matrix.length;
        if (n == 1) return matrix[0][0];
        if (n == 2) return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];

        double det = 0;
        for (int j = 0; j < n; j++) {
            double[][] subMatrix = getSubMatrix(matrix, 0, j);
            det += Math.pow(-1, j) * matrix[0][j] * determinant(subMatrix);
        }
        return det;
    }

    /**
     * Обратная матрица (только для квадратных невырожденных)
     */
    public static double[][] inverse(double[][] matrix) {
        if (!isSquare(matrix)) {
            throw new IllegalArgumentException("Matrix must be square");
        }

        int n = matrix.length;
        double det = determinant(matrix);

        if (Math.abs(det) < EPSILON) {
            throw new IllegalArgumentException("Matrix is singular (determinant = " + det + ")");
        }

        if (n == 1) {
            return new double[][] { { 1.0 / det } };
        }

        double[][] cofactors = getCofactorMatrix(matrix);
        double[][] adjugate = transpose(cofactors);
        return multiplyByScalar(adjugate, 1.0 / det);
    }

    /**
     * Клонирование матрицы
     */
    public static double[][] clone(double[][] matrix) {
        if (matrix == null) {
            throw new IllegalArgumentException("Matrix cannot be null");
        }

        int rows = matrix.length;
        int cols = matrix[0].length;
        double[][] result = createMatrix(rows, cols);

        for (int i = 0; i < rows; i++) {
            System.arraycopy(matrix[i], 0, result[i], 0, cols);
        }
        return result;
    }


    /**
     * Вектор параметра для кубических кривых: [t³, t², t, 1]
     */
    public static double[] getCubicParameterVector(double t) {
        double t2 = t * t;
        double t3 = t2 * t;
        return new double[] { t3, t2, t, 1.0 };
    }

    /**
     * Вычисление точки на кривой по коэффициентам
     * @param coefficients матрица коэффициентов [4][2]
     * @param t параметр [0, 1]
     * @return точка на кривой
     */
    public static Point evaluateCubicCurve(double[][] coefficients, double t) {
        double t2 = t * t;
        double t3 = t2 * t;

        int x = (int) Math.round(
                coefficients[0][0] * t3 +
                        coefficients[1][0] * t2 +
                        coefficients[2][0] * t +
                        coefficients[3][0]
        );

        int y = (int) Math.round(
                coefficients[0][1] * t3 +
                        coefficients[1][1] * t2 +
                        coefficients[2][1] * t +
                        coefficients[3][1]
        );

        return new Point(x, y);
    }

    /**
     * Вычисление точки на кривой с предвычисленными степенями t
     */
    public static Point evaluateCubicCurve(double[][] coefficients, double t, double t2, double t3) {
        int x = (int) Math.round(
                coefficients[0][0] * t3 +
                        coefficients[1][0] * t2 +
                        coefficients[2][0] * t +
                        coefficients[3][0]
        );

        int y = (int) Math.round(
                coefficients[0][1] * t3 +
                        coefficients[1][1] * t2 +
                        coefficients[2][1] * t +
                        coefficients[3][1]
        );

        return new Point(x, y);
    }


    /**
     * Проверка: матрица квадратная
     */
    public static boolean isSquare(double[][] matrix) {
        return matrix != null && matrix.length > 0 && matrix.length == matrix[0].length;
    }

    /**
     * Проверка: одинаковые размеры матриц
     */
    public static boolean haveSameDimensions(double[][] a, double[][] b) {
        return a != null && b != null && a.length == b.length && a[0].length == b[0].length;
    }

    /**
     * Получение подматрицы (для вычисления определителя)
     */
    public static double[][] getSubMatrix(double[][] matrix, int rowToRemove, int colToRemove) {
        int n = matrix.length;
        double[][] subMatrix = createMatrix(n - 1, n - 1);

        for (int i = 0; i < n; i++) {
            if (i == rowToRemove) continue;
            for (int j = 0; j < n; j++) {
                if (j == colToRemove) continue;
                subMatrix[i < rowToRemove ? i : i - 1][j < colToRemove ? j : j - 1] = matrix[i][j];
            }
        }
        return subMatrix;
    }

    /**
     * Матрица алгебраических дополнений
     */
    public static double[][] getCofactorMatrix(double[][] matrix) {
        int n = matrix.length;
        double[][] cofactors = createMatrix(n, n);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double[][] subMatrix = getSubMatrix(matrix, i, j);
                cofactors[i][j] = Math.pow(-1, i + j) * determinant(subMatrix);
            }
        }
        return cofactors;
    }

    /**
     * Вывод матрицы в консоль (для отладки)
     */
    public static void printMatrix(double[][] matrix) {
        if (matrix == null) {
            System.out.println("null");
            return;
        }
        for (double[] row : matrix) {
            for (double val : row) {
                System.out.printf("%10.4f ", val);
            }
            System.out.println();
        }
    }

    /**
     * Вывод вектора в консоль (для отладки)
     */
    public static void printVector(double[] vector) {
        if (vector == null) {
            System.out.println("null");
            return;
        }
        System.out.print("[");
        for (int i = 0; i < vector.length; i++) {
            System.out.printf("%10.4f", vector[i]);
            if (i < vector.length - 1) System.out.print(", ");
        }
        System.out.println("]");
    }



    private static void validateSameDimensions(double[][] a, double[][] b) {
        if (!haveSameDimensions(a, b)) {
            throw new IllegalArgumentException(
                    String.format("Matrix dimensions mismatch: %dx%d vs %dx%d",
                            a.length, a[0].length, b.length, b[0].length)
            );
        }
    }
}