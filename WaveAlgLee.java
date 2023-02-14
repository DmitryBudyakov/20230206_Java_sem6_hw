// Реализация волнового алгоритма Ли

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * WaveAlgLee
 */
public class WaveAlgLee {

    public static final int WALL = -9;
    public static final int EMPTY = -1;
    public static final int START = 0;
    public static final int END = -2;
    public static final int[][] OFFSETS = {
        { -1, 0 },
        { 0, -1 },
        { 1, 0 },
        { 0, 1 }
    };

    public static void main(String[] args) {

        int m = 15; // количество строк
        int n = 15; // количество столбцов
        int[] startPoint = { 2, 2 };    // начальная точка
        int[] endPoint = { 3, 9 };      // точка окончания
        int[][] room = createMatrix(m, n);  // пустая матрица (только контур без точек начала и окончания и без стен)

        // позиции стен в лабиринте: строка, столбец
        int[][] wallPos = {
                { 1, 4 },
                { 2, 4 },
                { 3, 4 },
                { 4, 4 },
                { 7, 1 },
                { 7, 2 },
                { 7, 3 },
                { 10, 4 },
                { 11, 4 },
                { 12, 4 },
                { 13, 4 },
                { 3, 7 },
                { 4, 7 },
                { 5, 7 },
                { 5, 6 },
                { 5, 8 },
                { 9, 7 },
                { 9, 6 },
                { 9, 8 },
                { 10, 7 },
                { 11, 7 },
                { 1, 10 },
                { 2, 10 },
                { 3, 10 },
                { 4, 10 },
                { 10, 10 },
                { 11, 10 },
                { 12, 10 },
                { 13, 10 },
                { 7, 11 },
                { 7, 12 },
                { 7, 13 }
        };

        // Инициализация матрицы (добавление начала, конца и стен):
        int[][] initedMatrix = initMatrix(room, startPoint, endPoint, wallPos);
        // копирование начальной матрицы:
        int[][] origMatrix = copyIntMatrix2D(initedMatrix);

        System.out.println("Исходное состояние:");
        printVisualMatrix(initedMatrix);
        System.out.println();

        // Поиск пути от начала до конца:
        int[][] paths = findPath(initedMatrix, startPoint, endPoint, OFFSETS);

        // Вывод результата поиска:
        System.out.println("Результат:");
        System.out.printf("Начало: %s ", Arrays.toString(startPoint));
        System.out.printf("Конец: %s ", Arrays.toString(endPoint));
        if (paths[endPoint[0]][endPoint[1]] == END) {
            System.out.printf("Длина пути: Нет пути\n");
        } else {
            System.out.printf("Длина пути: %d\n", paths[endPoint[0]][endPoint[1]]);
        }
        System.out.println();
        System.out.println("Результат поиска пути:");
        printVisualMatrix(paths);

        // отображение кратчайшего маршрута от начала до конца
        System.out.println();
        System.out.println("Маршрут:");
        List<int[]> routes = getShortestPath(paths, endPoint);
        for (int i = 1; i < routes.size(); i++) {
            if (i == 1 && routes.size() == 2) {
                System.out.printf("S -> %s -> E\n", Arrays.toString(routes.get(i)));
            } else if (i == 1) {
                System.out.printf("S -> %s", Arrays.toString(routes.get(i)));
            } else if (i == routes.size() - 1) {
                System.out.printf("-> %s -> E\n", Arrays.toString(routes.get(i)));
            } else {
                System.out.printf(" -> %s", Arrays.toString(routes.get(i)));
            }
            
        }

        System.out.println();
        // вывод матрицы с маршрутом
        System.out.println("Маршрут на карте:");
        for (int i = 1; i < routes.size(); i++) {
            origMatrix[routes.get(i)[0]][routes.get(i)[1]] = i;
        }
        printVisualMatrix(origMatrix);

    }

    /**
     * Создает матрицу размера M x N
     * @param M - кол-во строк
     * @param M - кол-во столбцов
     * @return
     */
    public static int[][] createMatrix(int M, int N) {
        int[][] matrix = new int[M][N];
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                if (i == 0 || i == M - 1 || j == 0 || j == N - 1) {
                    matrix[i][j] = WALL;
                } else {
                    matrix[i][j] = EMPTY;
                }
            }
        }
        return matrix;
    }

    /**
     * Копирование 2D int матрицы
     * @param matrix - 2D int матрица
     * @return
     */
    public static int[][] copyIntMatrix2D(int[][] matrix) {
        int[][] copyMatrix = new int[matrix.length][matrix[0].length];
        for (int i = 0; i < copyMatrix.length; i++) {
            for (int j = 0; j < copyMatrix[i].length; j++) {
                copyMatrix[i][j] = matrix[i][j];
            }
        }
        return copyMatrix;
    }

    /**
     * Вывод в консоль сырой матрицы
     * 
     * @param matrix
     */
    public static void printRawMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (j == matrix[i].length - 1)
                    System.out.printf("%4d\n", matrix[i][j]);
                else {
                    if (matrix[i][j] == START)
                        System.out.printf("%4d", matrix[i][j]);
                    else
                        System.out.printf("%4d", matrix[i][j]);
                }
            }
        }
    }

    /**
     * Вывод матрицы в удобочитаемом виде
     * @param matrix
     */
    public static void printVisualMatrix(int[][] matrix) {
        String signWall = "#";
        String signEmpty = ".";
        String signStart = "S";
        String signEnd = "E";
        String ret = "";

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (j == matrix[0].length - 1) {
                    ret = "\n";
                } else
                    ret = "";
                if (matrix[i][j] == WALL)
                    System.out.printf("%4s%s", signWall, ret);
                if (matrix[i][j] == EMPTY)
                    System.out.printf("%4s%s", signEmpty, ret);
                if (matrix[i][j] == START)
                    System.out.printf("%4s%s", signStart, ret);
                if (matrix[i][j] == END)
                    System.out.printf("%4s%s", signEnd, ret);
                if (matrix[i][j] != WALL &&
                    matrix[i][j] != EMPTY &&
                    matrix[i][j] != START &&
                    matrix[i][j] != END
                    ) System.out.printf("%4d%s", matrix[i][j], ret);
            }
        }
    }

    /**
     * Инициализация матрицы. Добавление точек старта и окончания и препятствий.
     * 
     * @param matrix - пустая матрица
     * @param start - точка начала
     * @param end - точка окончания
     * @param walls  - стены
     * @return
     */
    public static int[][] initMatrix(int[][] matrix, int[] start, int[] end, int[][] walls) {
        matrix[start[0]][start[1]] = START;
        matrix[end[0]][end[1]] = END;

        for (int[] wall : walls) {
            matrix[wall[0]][wall[1]] = WALL;
        }

        return matrix;
    }

    /**
     * Алгоритм поиска пути от начала до конца
     * @param matrix - проинициализированная матрица (с точками старта, финиша и стенами)
     * @param start - точка начала
     * @param end - точка окончания
     * @param OFFSETS - алгоритм проверки соседних точек
     * @return
     */
    public static int[][] findPath(int[][] matrix, int[] start, int[] end, int[][] OFFSETS) {
        int rows = matrix.length;
        int columns = matrix[0].length;
        Boolean[][] looked = new Boolean[rows][columns];
        for (int i = 0; i < looked.length; i++) {
            for (int j = 0; j < looked[i].length; j++) {
                looked[i][j] = false;
            }
        }

        // очередь для обследуемых точек
        Queue<int[]> queue = new LinkedList<>();
        matrix[start[0]][start[1]] = 0;
        looked[start[0]][start[1]] = true;

        // добавление в очередь точки начала
        queue.add(start);

        int curX;
        int curY;
        int newX;
        int newY;
        while (queue.size() != 0) {
            int[] cur = queue.remove();
            curX = cur[0];
            curY = cur[1];
            for (int[] newPoint : OFFSETS) {
                newX = curX + newPoint[0];
                newY = curY + newPoint[1];
                if (
                    (newX > 0 && newX < rows) && 
                    (newY > 0 && newY < columns) && 
                    !looked[newX][newY] && 
                    matrix[newX][newY] != WALL) {
                        matrix[newX][newY] = matrix[curX][curY] + 1;
                        looked[newX][newY] = true;
                        queue.add(new int[]{newX,newY});
                }
            }
        }
        return matrix;
    }

    /**
     * Определение кратчайшего маршрута от начала до конца
     * @param matrix - матрица с найденным расстоянием от начала до конца
     * @param end - конечная точка
     * @return
     */
    public static List<int[]> getShortestPath(int[][] matrix, int[] end) {
        ArrayList<int[]> route = new ArrayList<>();
        int[] curE = end;
        int curEx = curE[0];
        int curEy = curE[1];
        int prevX;
        int prevY;
        while (matrix[curEx][curEy] != START) {
            for (int[] path : OFFSETS) {
                prevX = curEx + path[0];
                prevY = curEy + path[1];
                if (matrix[prevX][prevY] == matrix[curEx][curEy] - 1) {
                    route.add(new int[]{prevX,prevY});
                    curEx = prevX;
                    curEy = prevY;
                }
            }
        }
        // разворот маршрута от начала до конца
        Collections.reverse(route);
        return route;
    }
}