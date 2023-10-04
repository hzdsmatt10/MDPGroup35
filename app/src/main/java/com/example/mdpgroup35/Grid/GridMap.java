package com.example.mdpgroup35.Grid;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;

import com.example.mdpgroup35.State.State;
import com.example.mdpgroup35.Bluetooth.BluetoothUtils;
import com.example.mdpgroup35.R;

import com.example.mdpgroup35.RpiHelper.Response;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GridMap extends View {

    public GridMap(Context c) {
        super(c);
        initMap();
    }

    private Paint whitePaint = new Paint();
    private Paint whiteTextPaint = new Paint();
    private Paint blackPaint = new Paint();
    private Paint obstacleColor = new Paint();
    private Paint robotColor = new Paint();
    private Paint startColor = new Paint();
    private Paint unexploredColor = new Paint();
    private Paint exploredColor = new Paint();
    private Paint obstacleDirectionColor = new Paint();
    private Paint fastestPathColor = new Paint();
    private boolean bluetoothMessageHandled = false; // Class-level variable


    private static String robotDirection = "None";
    private static int[] startCoord = new int[]{-1, -1};
    private static int[] curCoord = new int[]{-1, -1};
    private static int[] prevCoord = new int[]{-1, -1};
    private static ArrayList<int[]> obstacleCoord = new ArrayList<>();
    private static ArrayList<String[]> obstacleDirectionCoord = new ArrayList<>();
    private static ArrayList<String[]> prevObstacleDirectionCoord = new ArrayList<>();
    private static int[] prevStartCoord = new int[]{-1, -1};
    private static String prevRobotDirection;
    private static boolean canDrawRobot = false;
    private static boolean setEditStatus = false;
    private static boolean startCoordStatus = false;
    private static boolean setObstacleStatus = false;
    private static boolean setNorthObstacleStatus = false;
    private static boolean setSouthObstacleStatus = false;
    private static boolean setWestObstacleStatus = false;
    private static boolean setEastObstacleStatus = false;
    private static boolean setRotateLeftStatus = false;
    private static boolean setRotateRightStatus = false;
    private static boolean setEditDirectionObstacleStatus = false;
    private static boolean setEditStartCoordStatus = false;
    private static boolean unSetCellStatus = false;
    private static boolean setExploredStatus = false;
    private Bitmap obstacleDirectionBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_baseline_clear_24);
    private Bitmap robotDirectionBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_baseline_clear_24);

    private static final String TAG = "GridMap";
    private static final int COL = 20;
    private static final int ROW = 20;
    private static float cellSize;
    private static final char[] direction = new char[]{'N', 'E', 'S', 'W'};
    public static Cell[][] cells;
    private String messageToBot;
    private Map<Integer, String> obstacleTargetMapping = new HashMap<>();
    private boolean mapDrawn = false;
    public GridMap(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initMap();
        blackPaint.setColor(Color.BLACK);
        blackPaint.setStrokeWidth(3);
        whitePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        whitePaint.setColor(getResources().getColor(R.color.white));
        whitePaint.setTextSize(16);
        //change Text size of the obstacle
        whiteTextPaint.setColor(getResources().getColor(R.color.white));
        whiteTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        whiteTextPaint.setTextSize(24);
        obstacleColor.setColor(getResources().getColor(R.color.green_700));
        robotColor.setColor(getResources().getColor(R.color.teal_200));
        startColor.setColor(Color.CYAN);
        unexploredColor.setColor(getResources().getColor(R.color.green_200));
        exploredColor.setColor(Color.WHITE);
        obstacleDirectionColor.setColor(Color.WHITE);
        fastestPathColor.setColor(getResources().getColor(R.color.green_500));
    }

    private void initMap() {
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        showLog("Entering onDraw");
        super.onDraw(canvas);
        showLog("Redrawing map");

        //CREATE CELL COORDINATES
        showLog("Creating Cell");

        if (!mapDrawn) {
            this.createCell();
            mapDrawn = true;
        }

        drawIndividualCell(canvas);
        drawHorizontalLines(canvas);
        drawVerticalLines(canvas);
        drawGridNumber(canvas);
        if (getCanDrawRobot())
            drawRobot(canvas, curCoord);
        drawObstacleWithDirection(canvas, obstacleDirectionCoord);
        drawImageIdentified(canvas);

        showLog("Exiting onDraw");
    }

    private void drawIndividualCell(Canvas canvas) {
        showLog("Entering drawIndividualCell");
        for (int x = 1; x <= COL; x++)
            for (int y = 0; y < ROW; y++)
                if (!cells[x][y].type.equals("image") && cells[x][y].getId().equals("-1")) {
                    canvas.drawRect(cells[x][y].startX, cells[x][y].startY, cells[x][y].endX, cells[x][y].endY, cells[x][y].paint);
                } else {
                    Paint cellPaint = new Paint();
                    cellPaint.setTextSize(20);
                    cellPaint.setColor(Color.WHITE);
                    cellPaint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawRect(cells[x][y].startX, cells[x][y].startY, cells[x][y].endX, cells[x][y].endY, cells[x][y].paint);
//                    canvas.drawText(cells[x][y].getId(), (cells[x][y].startX + cells[x][y].endX) / 2, cells[x][y].endY + (cells[x][y].startY - cells[x][y].endY) / 4, cellPaint);
                }

        showLog("Exiting drawIndividualCell");
    }

    public void drawImageNumberCell(int x, int y, String direction, String obstacleId, String imageId) {
        showLog("Entering drawImageNumberCell");
        // Remove existing cell
        for (int i = 0; i < obstacleDirectionCoord.size(); i++) {
            String[] s = obstacleDirectionCoord.get(i);
            if (s[0].equalsIgnoreCase(String.valueOf(x)) && s[1].equalsIgnoreCase(String.valueOf(y)) && s[2].equalsIgnoreCase(direction)) {
                obstacleDirectionCoord.remove(i);
                break;
            }

        }

        cells[x][20 - y].setType("image");
        cells[x][20 - y].setId(obstacleId);
        String[] obstacleDirCoord = new String[5];
        obstacleDirCoord[0] = String.valueOf(x);
        obstacleDirCoord[1] = String.valueOf(y);
        obstacleDirCoord[2] = direction;
        obstacleDirCoord[3] = String.valueOf(obstacleId);
        obstacleDirCoord[4] = imageId;
        this.getObstacleDirectionCoord().add(obstacleDirCoord);
        this.invalidate();
        showLog("Exiting drawImageNumberCell");
    }

    private void drawHorizontalLines(Canvas canvas) {
        for (int y = 0; y <= ROW; y++) {
            canvas.drawLine(cells[1][y].startX, cells[1][y].startY - (cellSize / 30), cells[20][y].endX, cells[20][y].startY - (cellSize / 30), blackPaint);
        }
    }

    private void drawVerticalLines(Canvas canvas) {
        for (int x = 0; x <= COL; x++)
            canvas.drawLine(cells[x][0].startX - (cellSize / 30) + cellSize, cells[x][0].startY - (cellSize / 30), cells[x][0].startX - (cellSize / 30) + cellSize, cells[x][19].endY + (cellSize / 30), blackPaint);
    }

    private void drawGridNumber(Canvas canvas) {
        showLog("Entering drawGridNumber");
        for (int x = 1; x <= COL; x++) {
            if (x > 9)
                canvas.drawText(Integer.toString(x - 1), cells[x][20].startX + (cellSize / 5), cells[x][20].startY + (cellSize / 2), whitePaint);
            else
                canvas.drawText(Integer.toString(x - 1), cells[x][20].startX + (cellSize / 3), cells[x][20].startY + (cellSize / 2), whitePaint);
        }
        for (int y = 0; y < ROW; y++) {
            if ((20 - y) > 10)
                canvas.drawText(Integer.toString(19 - y), cells[0][y].startX + (cellSize / 4), cells[0][y].startY + (cellSize / 1.5f), whitePaint);
            else
                canvas.drawText(Integer.toString(19 - y), cells[0][y].startX + (cellSize / 2f), cells[0][y].startY + (cellSize / 1.5f), whitePaint);
        }
        showLog("Exiting drawGridNumber");
    }

    public ArrayList<State> getObstacles() {


        ArrayList<State> obstacles = new ArrayList<>();

        for (String[] s : obstacleDirectionCoord)
            obstacles.add(new State(
                    Integer.parseInt(s[0]) - 1,
                    Integer.parseInt(s[1]) - 1,
                    s[2]
            ));
        return obstacles;
    }

    private ArrayList<String[]> getObstacleDirectionCoord() {
        return obstacleDirectionCoord;
    }

    public String getRobotDirection() {
        return robotDirection;
    }

    public boolean getMapDrawn() {
        return mapDrawn;
    }

    public void setRotateLeftStatus(boolean status) {
        setRotateLeftStatus = status;
    }

    public boolean getRotateLeftStatus() {
        return setRotateLeftStatus;
    }

    public void setRotateRightStatus(boolean status) {
        setRotateRightStatus = status;
    }

    public boolean getRotateRightStatus() {
        return setRotateRightStatus;
    }

    public void setUnSetCellStatus(boolean status) {
        unSetCellStatus = status;
    }

    public boolean getUnSetCellStatus() {
        return unSetCellStatus;
    }

    public void setSetObstacleStatus(boolean status) {
        setObstacleStatus = status;
    }

    public boolean getSetObstacleStatus() {
        return setObstacleStatus;
    }

    public void setSetNorthObstacleStatus(boolean northObstacleStatus) {
        setNorthObstacleStatus = northObstacleStatus;
    }

    public boolean getSetNorthObstacleStatus() {
        return setNorthObstacleStatus;
    }

    public void setSetSouthObstacleStatus(boolean southObstacleStatus) {
        setSouthObstacleStatus = southObstacleStatus;
    }

    public boolean getSetSouthObstacleStatus() {
        return setSouthObstacleStatus;
    }

    public void setSetEastObstacleStatus(boolean eastObstacleStatus) {
        setEastObstacleStatus = eastObstacleStatus;
    }

    public boolean getSetEastObstacleStatus() {
        return setEastObstacleStatus;
    }

    public void setSetWestObstacleStatus(boolean westObstacleStatus) {
        setWestObstacleStatus = westObstacleStatus;
    }

    public boolean getSetWestObstacleStatus() {
        return setWestObstacleStatus;
    }

    public void setExploredStatus(boolean status) {
        setExploredStatus = status;
    }

    public boolean getExploredStatus() {
        return setExploredStatus;
    }

    public void setEditMapStatus(boolean status) {
        setEditStatus = status;
    }

    public void setStartCoordStatus(boolean status) {
        startCoordStatus = status;
    }

    private boolean getStartCoordStatus() {
        return startCoordStatus;
    }

    public boolean getCanDrawRobot() {
        return canDrawRobot;
    }

//    private int getImage(int imageId) {
//        return images[imageId - 11];
//    }

    private void createCell() {
        showLog("Entering cellCreate");
        cells = new Cell[COL + 1][ROW + 1];
        this.calculateDimension();
        cellSize = this.getCellSize();

        for (int x = 0; x <= COL; x++)
            for (int y = 0; y <= ROW; y++)
                cells[x][y] = new Cell(x * cellSize + (cellSize / 30), y * cellSize + (cellSize / 30), (x + 1) * cellSize, (y + 1) * cellSize, unexploredColor, "unexplored");
        showLog("Exiting createCell");
    }

    public void setStartCoord(int col, int row) {
        showLog("Entering setStartCoord");
        startCoord[0] = col;
        startCoord[1] = row;
        String direction = getRobotDirection();
        if (direction.equals("None")) {
            direction = "N";
        }
        if (this.getStartCoordStatus() || setEditStartCoordStatus)
            this.setCurCoord(col, row, direction);
        showLog("Exiting setStartCoord");
    }

    private int[] getStartCoord() {
        return startCoord;
    }

    public void setCurCoord(int col, int row, String direction) {
        showLog("Entering setCurCoord");
        curCoord[0] = col;
        curCoord[1] = row;
        this.setRobotDirection(direction);
        this.updateRobotAxis(col, row, direction.toUpperCase());

        row = this.convertRow(row);

        if (col >= 1 && col <= COL && row >= 1 && row <= ROW) {
            cells[col][row].setType("robot");
        }
        //for (int x = col - 1; x <= col + 1; x++)
        //  for (int y = row - 1; y <= row + 1; y++)
        //    if (x <= COL && (20 - y) <= ROW && x >= 1 && (20 - y) >= 1)
        //      cells[x][y].setType("robot");
        showLog("Exiting setCurCoord");
    }

    public int[] getCurCoord() {
        return curCoord;
    }

    private void calculateDimension() {
        showLog("Phone Width is" + String.valueOf(getWidth()));
        this.setCellSize(getWidth() / (COL + 1));
    }

    private int convertRow(int row) {
        return (20 - row);
    }

    private void setCellSize(float cellSize) {
        GridMap.cellSize = cellSize;
    }

    private float getCellSize() {
        return cellSize;
    }

    private void setOldRobotCoord(int oldCol, int oldRow) {
        showLog("Entering setOldRobotCoord");
        prevCoord[0] = oldCol;
        prevCoord[1] = oldRow;
        oldRow = this.convertRow(oldRow);
        for (int x = oldCol - 1; x <= oldCol + 1; x++)
            for (int y = oldRow - 1; y <= oldRow + 1; y++)
                if (x <= COL && (20 - y) <= ROW && x >= 1 && (20 - y) >= 1)
                    cells[x][y].setType("explored");
        showLog("Exiting setOldRobotCoord");
    }

    private int[] getOldRobotCoord() {
        return prevCoord;
    }

    private void obstacleRotateLeft(int col, int row) {
        showLog("Entering obstacleRotateLeft");
        for (int i = 0; i < getObstacleDirectionCoord().size(); i++) {
            if (getObstacleDirectionCoord().get(i)[0].equals(String.valueOf(col)) && getObstacleDirectionCoord().get(i)[1].equals(String.valueOf(row))) {
                for (int j = 0; j < direction.length; j++) {
                    if (String.valueOf(direction[j]).equals(getObstacleDirectionCoord().get(i)[2])) {
                        if (j == 0)
                            getObstacleDirectionCoord().get(i)[2] = String.valueOf(direction[3]);
                        else
                            getObstacleDirectionCoord().get(i)[2] = String.valueOf(direction[j - 1]);
                        break;
                    }
                }
            }
        }
        showLog("Exiting obstacleRotateLeft");
    }

    private void obstacleRotateRight(int col, int row) {
        showLog("Entering obstacleRotateRight");
        for (int i = 0; i < getObstacleDirectionCoord().size(); i++) {
            if (getObstacleDirectionCoord().get(i)[0].equals(String.valueOf(col)) && getObstacleDirectionCoord().get(i)[1].equals(String.valueOf(row))) {
                for (int j = 0; j < direction.length; j++) {
                    showLog(getObstacleDirectionCoord().get(i)[2]);
                    if (Character.toString(direction[j]).equals(getObstacleDirectionCoord().get(i)[2])) {
                        if (j == 3)
                            getObstacleDirectionCoord().get(i)[2] = String.valueOf(direction[0]);
                        else
                            getObstacleDirectionCoord().get(i)[2] = String.valueOf(direction[j + 1]);
                        break;
                    }
                }
            }
        }
        showLog("Exiting obstacleRotateRight");
    }

    private void setObstacleDirectionCoordinate(int col, int row, String obstacleDirection, int obstacleID) {
        showLog("Entering setObstacleDirectionCoordinate");
        String[] obstacleDirCoord = new String[5];
        obstacleDirCoord[0] = String.valueOf(col);
        obstacleDirCoord[1] = String.valueOf(row);
        obstacleDirCoord[2] = obstacleDirection;
        obstacleDirCoord[3] = String.valueOf(obstacleID);
        obstacleDirCoord[4] = "IMAGE_NULL";
        this.getObstacleDirectionCoord().add(obstacleDirCoord);

        row = convertRow(row);
        cells[col][row].setType("obstacleDirection");
        showLog("Exiting setObstacleDirectionCoordinate");
    }

    public void setRobotDirection(String direction) {
        robotDirection = direction;
        this.invalidate();
        ;
    }

    public void updateRobotAxis(int col, int row, String direction) {
        TextView xAxisTextView = ((Activity) this.getContext()).findViewById(R.id.xAxisTextView);
        TextView yAxisTextView = ((Activity) this.getContext()).findViewById(R.id.yAxisTextView);
        TextView dirTextView = ((Activity) this.getContext()).findViewById(R.id.dirTextView);

        xAxisTextView.setText(String.valueOf(col - 1));
        yAxisTextView.setText(String.valueOf(row - 1));
        //xAxisTextView.setText(String.valueOf(col));
        //yAxisTextView.setText(String.valueOf(row));
        String dirText = String.format("%s", direction);
        dirTextView.setText(dirText);
    }

    private void showObstaclePlot(int col, int row){
        TextView xAxisTextView = ((Activity) this.getContext()).findViewById(R.id.xAxisObstacleTextView);
        TextView yAxisTextView = ((Activity) this.getContext()).findViewById(R.id.yAxisObstacleTextView);

        xAxisTextView.setText(String.valueOf(col - 1));
        yAxisTextView.setText(String.valueOf(row - 1));
    }

    private void setObstacleCoord(int col, int row) {
        showLog("Entering setObstacleCoord");
        int[] obstacleCoord = new int[]{col, row};
        GridMap.obstacleCoord.add(obstacleCoord);
        row = this.convertRow(row);
        cells[col][row].setType("obstacle");
        showLog("Exiting setObstacleCoord");
    }


    public void updateImageWithID(String message) {
        int col;
        int row;
        int imageID;
        String direction;

        String[] parsed = message.split(" ");
        col = Integer.parseInt(parsed[1]);
        row = Integer.parseInt(parsed[2]);
        imageID = Integer.parseInt(parsed[3]);
        direction = parsed[4];
        showLog("Entering updateImageWithID");

        String strImageID = String.valueOf(imageID);
        int obstacleId = obstacleDirectionCoord.size() + 1;

        drawImageNumberCell(col, row, direction, String.valueOf(obstacleId), strImageID);
        showLog("Drawing obstacle ID: " + strImageID + " at " + col + "," + row);

    }

    public void updateImageWithID(Response res) {
        showLog("Entering updateImageWithID");
        String direction;
        boolean exist = false;

        String[] parsed = res.coordinate.split(",");
        int col = Integer.parseInt(parsed[0]) + 1;
        int row = Integer.parseInt(parsed[1]) + 1;
        int dir = Integer.parseInt(parsed[2]);
        direction = State.directionToCompass(dir);

        // Check if exist
        for (String[] s : obstacleDirectionCoord) {
            if (s[0].equalsIgnoreCase(String.valueOf(col)) && s[1].equalsIgnoreCase(String.valueOf(row)) && s[2].equalsIgnoreCase(direction)) {
                exist = true;
                break;
            }
        }

        if (!exist) return;

        String strImageID = res.result;
        int obstacleId = obstacleDirectionCoord.size() + 1;

        drawImageNumberCell(col, row, direction, String.valueOf(obstacleId), strImageID);
        showLog("Drawing obstacle ID: " + strImageID + " at " + col + "," + row);
    }

    public void updateRobot(String input) {
        canDrawRobot = true;
        String[] parsed = input.split(" ");
        int col = Integer.parseInt(parsed[1]);
        int row = Integer.parseInt(parsed[2]);
        String direction = parsed[3];
        setOldRobotCoord(curCoord[0], curCoord[1]);
        setRobotDirection(direction);
        setCurCoord(col, row, direction);
        updateRobotAxis(col, row, direction);
    }

    public void updateRobot(Response res) {
        canDrawRobot = true;
        String[] parsed = res.coordinate.split(",");
        int col = Integer.parseInt(parsed[0]) + 1;
        int row = Integer.parseInt(parsed[1]) + 1;

        String direction = State.directionToCompass(Integer.parseInt(parsed[2]));


        setOldRobotCoord(curCoord[0], curCoord[1]);
        setRobotDirection("N");
        setCurCoord(col, row, direction);
        updateRobotAxis(col, row, direction);
    }

    public void moveRobot(String input) {
        showLog("In moverobot!");
        canDrawRobot = true;
        String[] parsed = input.split(" ");
        String command = parsed[0];
        setOldRobotCoord(curCoord[0], curCoord[1]);
        int[] oldCoord = getOldRobotCoord();
        int col = oldCoord[0];
        int row = oldCoord[1];
        String direction;
        command = String.valueOf(command.charAt(0));
        switch (command) {
            case "W":
                direction = getRobotDirection();
                switch (direction) {
                    case "N":
                        setRobotDirection(direction);
                        setCurCoord(col, row + 1, direction);
                        updateRobotAxis(col, row + 1, direction);
                        break;
                    case "S":
                        setRobotDirection(direction);
                        setCurCoord(col, row - 1, direction);
                        updateRobotAxis(col, row - 1, direction);
                        break;
                    case "E":
                        setRobotDirection(direction);
                        setCurCoord(col + 1, row, direction);
                        updateRobotAxis(col + 1, row, direction);
                        break;
                    case "W":
                        setRobotDirection(direction);
                        setCurCoord(col - 1, row, direction);
                        updateRobotAxis(col - 1, row, direction);
                        break;
                }
                break;
            case "R":
                direction = getRobotDirection();
                switch (direction) {
                    case "N":
                        setRobotDirection(direction);
                        setCurCoord(col, row - 1, direction);
                        updateRobotAxis(col, row - 1, direction);
                        break;
                    case "S":
                        setRobotDirection(direction);
                        setCurCoord(col, row + 1, direction);
                        updateRobotAxis(col, row + 1, direction);
                        break;
                    case "E":
                        setRobotDirection(direction);
                        setCurCoord(col - 1, row, direction);
                        updateRobotAxis(col - 1, row, direction);
                        break;
                    case "W":
                        setRobotDirection(direction);
                        setCurCoord(col + 1, row, direction);
                        updateRobotAxis(col + 1, row, direction);
                        break;
                }
                break;
            case "d":
                direction = getRobotDirection();
                switch (direction) {
                    case "N":
                        setRobotDirection("E");
                        setCurCoord(col + 1, row + 1, "E");
                        updateRobotAxis(col + 1, row + 1, "E");
                        break;
                    case "S":
                        setRobotDirection("W");
                        setCurCoord(col - 1, row - 1, "W");
                        updateRobotAxis(col - 1, row - 1, "W");
                        break;
                    case "E":
                        setRobotDirection("S");
                        setCurCoord(col + 1, row - 1, "S");
                        updateRobotAxis(col + 1, row - 1, "S");
                        break;
                    case "W":
                        setRobotDirection("N");
                        setCurCoord(col - 1, row + 1, "N");
                        updateRobotAxis(col - 1, row + 1, "N");
                        break;
                }
                break;
            case "a":
                direction = getRobotDirection();
                switch (direction) {
                    case "N":
                        setRobotDirection("W");
                        setCurCoord(col - 1, row + 1, "W");
                        updateRobotAxis(col - 1, row + 1, "W");
                        break;
                    case "S":
                        setRobotDirection("E");
                        setCurCoord(col + 1, row - 1, "E");
                        updateRobotAxis(col + 1, row - 1, "E");
                        break;
                    case "E":
                        setRobotDirection("N");
                        setCurCoord(col - 1, row + 1, "N");
                        updateRobotAxis(col - 1, row + 1, "N");
                        break;
                    case "W":
                        setRobotDirection("S");
                        setCurCoord(col + 1, row - 1, "S");
                        updateRobotAxis(col + 1, row - 1, "S");
                        break;
                }
                break;
            case "e":
                direction = getRobotDirection();
                switch (direction) {
                    case "N":
                        setRobotDirection("W");
                        setCurCoord(col + 1, row - 1, "W");
                        updateRobotAxis(col + 1, row - 1, "W");
                        break;
                    case "S":
                        setRobotDirection("E");
                        setCurCoord(col - 1, row + 1, "E");
                        updateRobotAxis(col - 1, row + 1, "E");
                        break;
                    case "E":
                        setRobotDirection("N");
                        setCurCoord(col - 1, row - 1, "N");
                        updateRobotAxis(col - 1, row - 1, "N");
                        break;
                    case "W":
                        setRobotDirection("S");
                        setCurCoord(col + 1, row + 1, "S");
                        updateRobotAxis(col + 1, row + 1, "S");
                        break;
                }
                break;

            case "q":
                direction = getRobotDirection();
                switch (direction) {
                    case "N":
                        setRobotDirection("E");
                        setCurCoord(col - 1, row - 1, "E");
                        updateRobotAxis(col - 1, row - 1, "E");
                        break;
                    case "S":
                        setRobotDirection("W");
                        setCurCoord(col + 1, row + 1, "W");
                        updateRobotAxis(col + 1, row + 1, "W");
                        break;
                    case "E":
                        setRobotDirection("S");
                        setCurCoord(col - 1, row + 1, "S");
                        updateRobotAxis(col - 1, row + 1, "S");
                        break;
                    case "W":
                        setRobotDirection("N");
                        setCurCoord(col + 1, row - 1, "N");
                        updateRobotAxis(col + 1, row - 1, "N");
                        break;
                }
                break;


        }

    }

    private ArrayList<int[]> getObstacleCoord() {
        return obstacleCoord;
    }

    private void showLog(String message) {
        Log.d(TAG, message);
    }

    public void handleTargetMessage(String message) {
        // Parse the "TARGET" message to extract Obstacle Number and Target ID
        String[] parts = message.split(",");
        if (parts.length == 3 && parts[0].equals("TARGET")) {
            int obstacleNumber = Integer.parseInt(parts[1].trim());
            String targetId = parts[2].trim();

            // Update the obstacleTargetMapping with the new Target ID
            obstacleTargetMapping.put(obstacleNumber, targetId);

            // Trigger a redraw of the obstacles on the canvas
            // You can call the method that redraws your obstacles here.
            // For example: redrawObstacles();
        }
    }

    public void handleBluetoothMessage(String bluetoothMessage) {
        // Split the Bluetooth message by comma to extract the parts
        bluetoothMessageHandled = true;

        String[] parts = bluetoothMessage.split(",");

        // Check if the message format is correct
        if (parts.length == 3 && parts[0].trim().equals("TARGET")) {
            String originalTargetId = parts[1].trim();
            String newTargetId = parts[2].trim();

            // Iterate through obstacleDirectionCoord to find and update the targetId
            for (int i = 0; i < obstacleDirectionCoord.size(); i++) {
                if (obstacleDirectionCoord.get(i)[3].equals(originalTargetId)) {
                    // Update the targetId
                    obstacleDirectionCoord.get(i)[3] = newTargetId;

                    // Redraw the canvas to reflect the updated targetId
                    invalidate();
                    break; // Exit the loop after the first match is found and updated
                }
            }
        }
    }

    private void drawObstacleWithDirection(Canvas canvas, ArrayList<String[]> obstacleDirectionCoord) {
        showLog("Entering drawObstacleWithDirection");
        RectF rect;
        Paint paint = new Paint();
        float largerTextSize = 30f;

        // Check if handleBluetoothMessage has been called
        if (bluetoothMessageHandled) {
            paint.setTextSize(largerTextSize); // Set larger text size
            bluetoothMessageHandled = false; // Reset the flag
        } else {
            // Set the default text size here if needed
            // paint.setTextSize(defaultTextSize);
        }

        for (int i = 0; i < obstacleDirectionCoord.size(); i++) {
            int col = Integer.parseInt(obstacleDirectionCoord.get(i)[0]);
            int row = convertRow(Integer.parseInt(obstacleDirectionCoord.get(i)[1]));
            rect = new RectF(col * cellSize, row * cellSize, (col + 1) * cellSize, (row + 1) * cellSize);

            String targetId = obstacleDirectionCoord.get(i)[3];
            String obstacleNumber = obstacleDirectionCoord.get(i)[0];
            Log.d("TargetID", "Value: " + targetId);
            Log.d("ObstacleNumber", "Value: " + obstacleNumber);

            // Check if a valid Target ID is present
            if (!TextUtils.isEmpty(targetId)) {
                // Display the Target ID if it exists.
                float textX = cellSize * col + cellSize / 2;
                float textY = cellSize * row + cellSize / 2;
                canvas.drawText(targetId, textX, textY, whitePaint);
            } else {
                // If no Target ID, display the obstacle number.
                String obstacleNumberText = obstacleDirectionCoord.get(i)[0]; // Use the obstacle number from the message
                float textX = cellSize * col + cellSize / 2;
                float textY = cellSize * row + cellSize / 2;
                canvas.drawText(obstacleNumberText, textX, textY, whitePaint);
            }





            switch (obstacleDirectionCoord.get(i)[2]) {
                case "N":
                    obstacleDirectionBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_north_obstacle);
                    break;
                case "E":
                    obstacleDirectionBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_east_obstacle);
                    break;
                case "S":
                    obstacleDirectionBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_south_obstacle);
                    break;
                case "W":
                    obstacleDirectionBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_west_obstacle);
                    break;
                default:
                    break;
            }
            canvas.drawBitmap(obstacleDirectionBitmap, null, rect, null);

            showLog("Exiting drawObstacleWithDirection");
        }
    }

    public void drawRobot(Canvas canvas, int[] curCoord) {
        showLog("Entering drawObstacleWithDirection");
        RectF rect;

        for (int i = 0; i < curCoord.length; i++) {
            int col = curCoord[0];
            int row = convertRow(curCoord[1]);
            rect = new RectF(col * cellSize, row * cellSize, (col + 1) * cellSize, (row + 1) * cellSize);
            switch (this.getRobotDirection()) {
                case "N":
                    robotDirectionBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_robot_north);
                    break;
                case "E":
                    robotDirectionBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_robot_east);
                    break;
                case "S":
                    robotDirectionBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_robot_south);
                    break;
                case "W":
                    robotDirectionBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_robot_west);
                    break;
                default:
                    break;
            }
            canvas.drawBitmap(robotDirectionBitmap, null, rect, null);
            showLog("Exiting drawObstacleWithDirection");
        }
    }

    //Display Recognised Image
    private void drawImageIdentified(Canvas canvas) {
        RectF rect;

        for (int i = 0; i < getObstacleDirectionCoord().size(); i++) {
            int col = Integer.parseInt(obstacleDirectionCoord.get(i)[0]);
            int row = convertRow(Integer.parseInt(obstacleDirectionCoord.get(i)[1]));
            int gap = 5;
            rect = new RectF((col * cellSize) + gap, (row * cellSize) + gap, ((col + 1) * cellSize), ((row + 1) * cellSize) - gap);
            String resultId = getObstacleDirectionCoord().get(i)[4];

            try {
                if (!getObstacleDirectionCoord().get(i)[4].equals("IMAGE_NULL") && !resultId.isEmpty()) {
                    canvas.drawRect(rect, blackPaint);
                    Paint cellPaint = new Paint();
                    cellPaint.setTextSize(40);
                    cellPaint.setColor(Color.WHITE);
                    cellPaint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText(resultId, cellSize * col + cellSize / 2, cellSize * row + cellSize /2, whiteTextPaint);
//                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
//                            mappedObstacles.get(resultId));
//                    canvas.drawBitmap(bitmap, null, rect, null);
                }
            } catch (Exception e) {

            }
        }
    }

    private class Cell {
        float startX, startY, endX, endY;
        Paint paint;
        String type;
        String id = "-1";

        private Cell(float startX, float startY, float endX, float endY, Paint paint, String type) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.paint = paint;
            this.type = type;
        }

        public void setType(String type) {
            this.type = type;
            switch (type) {
                case "obstacle":
                    this.paint = obstacleColor;
                    break;
                case "robot":
                    this.paint = robotColor;
                    break;
                case "start":
                    this.paint = startColor;
                    break;
                case "unexplored":
                    this.paint = unexploredColor;
                    break;
                case "explored":
                    this.paint = exploredColor;
                    break;
                case "obstacleDirection":
                    this.paint = obstacleColor;
                    break;
                case "fastestPath":
                    this.paint = fastestPathColor;
                    break;
                case "image":
                    this.paint = obstacleColor;
                default:
                    showLog("setTtype default: " + type);
                    break;
            }
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return this.id;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        showLog("Entering onTouchEvent");
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int column = (int) (event.getX() / cellSize);
            int row = this.convertRow((int) (event.getY() / cellSize));
            ToggleButton setStartPointToggleBtn = ((Activity) this.getContext()).findViewById(R.id.setStartPointToggleBtn);

            if (startCoordStatus) {
                if (canDrawRobot) {
                    int[] startCoord = this.getStartCoord();
                    if (startCoord[0] >= 1 && startCoord[1] >= 1) {
                        startCoord[1] = this.convertRow(startCoord[1]);
                        for (int x = startCoord[0] - 1; x <= startCoord[0] + 1; x++)
                            for (int y = startCoord[1] - 1; y <= startCoord[1] + 1; y++)
                                if (x <= COL && (20 - y) <= ROW && x >= 1 && (20 - y) >= 1)
                                    cells[x][y].setType("unexplored");
                    }
                } else
                    canDrawRobot = true;
                this.setStartCoord(column, row);
                startCoordStatus = false;
                // TODO ADD a way to change robot's orientation
                String direction = getRobotDirection();
                if (direction.equals("None")) {
                    direction = "N";
                }
                updateRobotAxis(column, row, direction);
                if (setStartPointToggleBtn.isChecked())
                    setStartPointToggleBtn.toggle();
                this.invalidate();
                return true;
            }
            if (setEditStatus) {

                for (int i = 0; i < obstacleDirectionCoord.size(); i++) {
                    if (obstacleDirectionCoord.get(i)[0].equals(Integer.toString(column)) && obstacleDirectionCoord.get(i)[1].equals(Integer.toString(row))) {
                        prevObstacleDirectionCoord = new ArrayList<>();
                        prevObstacleDirectionCoord.add(obstacleDirectionCoord.get(i));
                        setEditDirectionObstacleStatus = true;
                    }
                }

                for (int i = 0; i < startCoord.length; i++) {
                    showLog(startCoord[0] + " " + column + " " + startCoord[1] + " " + row);
                    if (startCoord[0] == column && startCoord[1] == row) {
                        showLog("TRUE");
                        prevStartCoord[0] = startCoord[0];
                        prevStartCoord[1] = startCoord[1];
                        prevRobotDirection = getRobotDirection();
                        setEditStartCoordStatus = true;
                    }
                }

                this.invalidate();
                return true;
            }
            if (setObstacleStatus) {
                this.setObstacleCoord(column, row);
                this.invalidate();
                return true;
            }
            if (setNorthObstacleStatus) {
                if (checkUnexploredCell(column, row))
                    this.setObstacleDirectionCoordinate(column, row, "N", obstacleDirectionCoord.size() + 1);
                showObstaclePlot(column,row);
                //messageToBot = String.format("Coordinates: (%d, %d), Direction: N", column, row);
                //BluetoothUtils.write(messageToBot.getBytes());
                this.invalidate();
                return true;
            }
            if (setSouthObstacleStatus) {
                if (checkUnexploredCell(column, row))
                    this.setObstacleDirectionCoordinate(column, row, "S", obstacleDirectionCoord.size() + 1);
                showObstaclePlot(column,row);
                // messageToBot = String.format("Coordinates: (%d, %d), Direction: S", column, row);
                // BluetoothUtils.write(messageToBot.getBytes());
                this.invalidate();
                return true;
            }
            if (setEastObstacleStatus) {
                if (checkUnexploredCell(column, row))
                    this.setObstacleDirectionCoordinate(column, row, "E", obstacleDirectionCoord.size() + 1);
                showObstaclePlot(column,row);
                // messageToBot = String.format("Coordinates: (%d, %d), Direction: E", column, row);
                // BluetoothUtils.write(messageToBot.getBytes());

                this.invalidate();
                return true;
            }
            if (setWestObstacleStatus) {
                if (checkUnexploredCell(column, row))
                    this.setObstacleDirectionCoordinate(column, row, "W", obstacleDirectionCoord.size() + 1);                    showObstaclePlot(column,row);
                showObstaclePlot(column,row);
                // messageToBot = String.format("Coordinates: (%d, %d), Direction: W", column, row);
                // BluetoothUtils.write(messageToBot.getBytes());

                this.invalidate();
                return true;
            }
            if (setRotateLeftStatus) {
                String obstacleTemp = null;
                this.obstacleRotateLeft(column, row);
                for (int i = 0; i < obstacleDirectionCoord.size(); i++) {
                    if (obstacleDirectionCoord.get(i)[0].equals(Integer.toString(column)) && obstacleDirectionCoord.get(i)[1].equals(Integer.toString(row))) {
                        obstacleTemp = obstacleDirectionCoord.get(i)[2];
                        break;
                    }
                }
//                if (obstacleTemp!=null) {
//                    String message = "Rotating anticlockwise for (" + column + "," + row + ") into " + obstacleTemp;
//                    BluetoothUtils.write(message.getBytes());
//                }
                this.invalidate();
                return true;
            }
            if (setRotateRightStatus) {
                String obstacleTemp = null;
                this.obstacleRotateRight(column, row);
                for (int i = 0; i < obstacleDirectionCoord.size(); i++) {
                    if (obstacleDirectionCoord.get(i)[0].equals(Integer.toString(column)) && obstacleDirectionCoord.get(i)[1].equals(Integer.toString(row))) {
                        obstacleTemp = obstacleDirectionCoord.get(i)[2];
                        break;
                    }
                }
//                if (obstacleTemp!=null) {
//                    String message = "Rotating clockwise for (" + column + "," + row + ") into " + obstacleTemp;
//                    BluetoothUtils.write(message.getBytes());
//                }
                this.invalidate();
                return true;
            }
            if (setExploredStatus) {
                cells[column][20 - row].setType("explored");
                this.invalidate();
                return true;
            }
            if (unSetCellStatus) {
                cells[column][20 - row].setType("unexplored");
                for (int i = 0; i < obstacleDirectionCoord.size(); i++) {
                    if (obstacleDirectionCoord.get(i)[0].equals(Integer.toString(column)) && obstacleDirectionCoord.get(i)[1].equals(Integer.toString(row))) {
                        obstacleDirectionCoord.remove(i);
                    }
                }
                this.invalidate();
                return true;
            }
        }

        if (event.getAction() == MotionEvent.ACTION_UP && setEditStatus) {
            int column = (int) (event.getX() / cellSize);
            int row = this.convertRow((int) (event.getY() / cellSize));

            if (setEditDirectionObstacleStatus) {
                if (column > COL || row > ROW || checkUnexploredCell(column, row)) {

                    for (int i = 0; i < prevObstacleDirectionCoord.size(); i++) {
                        cells[Integer.parseInt(prevObstacleDirectionCoord.get(i)[0])][20 - Integer.parseInt(prevObstacleDirectionCoord.get(i)[1])].setType("unexplored");
                        for (int j = 0; j < obstacleDirectionCoord.size(); j++) {
                            if (obstacleDirectionCoord.get(j)[0].equals(prevObstacleDirectionCoord.get(i)[0]) && obstacleDirectionCoord.get(j)[1].equals(prevObstacleDirectionCoord.get(i)[1])) {
                                // Convert to state to remove from obstacle hashmap
                                obstacleDirectionCoord.remove(j);
                            }
                        }
                        if (column <= COL && row <= ROW)
                            this.setObstacleDirectionCoordinate(column, row, prevObstacleDirectionCoord.get(i)[2], Integer.parseInt(prevObstacleDirectionCoord.get(i)[3]));
                    }
                    prevObstacleDirectionCoord = new ArrayList<>();
                    setEditDirectionObstacleStatus = false;

                    this.invalidate();
                    return true;
                }
            } else if (setEditStartCoordStatus) {
                if (column > COL || row > ROW || checkUnexploredCell(column, row)) {

                    int[] startCoord = this.getStartCoord();
                    if (startCoord[0] >= 1 && startCoord[1] >= 1) {
                        startCoord[1] = this.convertRow(startCoord[1]);
                        for (int x = startCoord[0] - 1; x <= startCoord[0] + 1; x++)
                            for (int y = startCoord[1] - 1; y <= startCoord[1] + 1; y++)
                                if (x <= COL && (20 - y) <= ROW && x >= 1 && (20 - y) >= 1)
                                    cells[x][y].setType("unexplored");
                    }
                    if (column <= 20 && row <= 20) {
                        this.setStartCoord(column, row);
                        this.setRobotDirection(prevRobotDirection);
                        String direction = getRobotDirection();
                        if (direction.equals("None")) {
                            direction = "N";
                        }
                        try {
                            int directionInt = 0;
                            if (direction.equals("N")) {
                                directionInt = 0;
                            } else if (direction.equals("W")) {
                                directionInt = 3;
                            } else if (direction.equals("E")) {
                                directionInt = 1;
                            } else if (direction.equals("S")) {
                                directionInt = 2;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println("update column is "+column);
                        System.out.println("update column is "+row);
                        System.out.println("update column is "+direction);
                        updateRobotAxis(column, row, direction);
                    } else {
                        canDrawRobot = false;
                        startCoord = new int[]{-1, -1};
                        curCoord = new int[]{-1, -1};
                        robotDirection = "None";
                    }
                    setEditStartCoordStatus = false;
                    this.invalidate();
                    return true;
                }
            }

        }
        showLog("Exiting onTouchEvent");
        return false;
    }

    private boolean checkUnexploredCell(int col, int row) {
        return cells[col][20 - row].type.equals("unexplored");
    }

    public void toggleCheckedBtn(String buttonName) {
        ToggleButton setEditToggleBtn = ((Activity) this.getContext()).findViewById(R.id.setEditToggleBtn);
        ToggleButton setStartPointToggleBtn = ((Activity) this.getContext()).findViewById(R.id.setStartPointToggleBtn);
        ToggleButton northObstacleToggleBtn = ((Activity) this.getContext()).findViewById(R.id.northObstacleToggleBtn);
        ToggleButton southObstacleToggleBtn = ((Activity) this.getContext()).findViewById(R.id.southObstacleToggleBtn);
        ToggleButton eastObstacleToggleBtn = ((Activity) this.getContext()).findViewById(R.id.eastObstacleToggleBtn);
        ToggleButton westObstacleToggleBtn = ((Activity) this.getContext()).findViewById(R.id.westObstacleToggleBtn);
        ToggleButton rotateLeftBtn = ((Activity) this.getContext()).findViewById(R.id.rotateLeftBtn);
        ToggleButton rotateRightBtn = ((Activity) this.getContext()).findViewById(R.id.rotateRightBtn);
        ToggleButton clearToggleBtn = ((Activity) this.getContext()).findViewById(R.id.clearToggleBtn);

        if (!buttonName.equals("setEditToggleBtn"))
            if (setEditToggleBtn.isChecked()) {
                this.setEditMapStatus(false);
                setEditToggleBtn.toggle();
            }

        if (!buttonName.equals("setStartPointToggleBtn"))
            if (setStartPointToggleBtn.isChecked()) {
                this.setStartCoordStatus(false);
                setStartPointToggleBtn.toggle();
            }

        if (!buttonName.equals("northObstacleToggleBtn"))
            if (northObstacleToggleBtn.isChecked()) {
                this.setSetNorthObstacleStatus(false);
                northObstacleToggleBtn.toggle();
            }

        if (!buttonName.equals("southObstacleToggleBtn"))
            if (southObstacleToggleBtn.isChecked()) {
                this.setSetSouthObstacleStatus(false);
                southObstacleToggleBtn.toggle();
            }

        if (!buttonName.equals("eastObstacleToggleBtn"))
            if (eastObstacleToggleBtn.isChecked()) {
                this.setSetEastObstacleStatus(false);
                eastObstacleToggleBtn.toggle();
            }

        if (!buttonName.equals("westObstacleToggleBtn"))
            if (westObstacleToggleBtn.isChecked()) {
                this.setSetWestObstacleStatus(false);
                westObstacleToggleBtn.toggle();
            }
        if (!buttonName.equals("rotateLeftToggleBtn"))
            if (rotateLeftBtn.isChecked()) {
                this.setRotateLeftStatus(false);
                rotateLeftBtn.toggle();
            }
        if (!buttonName.equals("rotateRightToggleBtn"))
            if (rotateRightBtn.isChecked()) {
                this.setRotateRightStatus(false);
                rotateRightBtn.toggle();
            }

        if (!buttonName.equals("clearToggleBtn"))
            if (clearToggleBtn.isChecked()) {
                this.setUnSetCellStatus(false);
                clearToggleBtn.toggle();
            }
    }


    public void resetMap() {
        showLog("Entering resetMap");
        updateRobotAxis(1, 1, "None");

        this.toggleCheckedBtn("None");

        startCoord = new int[]{-1, -1};
        curCoord = new int[]{-1, -1};
        prevCoord = new int[]{-1, -1};
        robotDirection = "None";
        obstacleDirectionCoord = new ArrayList<>();
        obstacleCoord = new ArrayList<>();
        mapDrawn = false;
        canDrawRobot = false;
        Bitmap arrowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_baseline_clear_24);
        obstacleDirectionBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_baseline_clear_24);

        prevObstacleDirectionCoord = new ArrayList<>();
        setEditDirectionObstacleStatus = false;
        setEditStartCoordStatus = false;

        showLog("Exiting resetMap");
        this.invalidate();
    }

    public void resetRobot() {
        showLog("Entering resetRobot");
        updateRobotAxis(3, 3, "None");

        this.toggleCheckedBtn("None");
        for (int x = 1; x <= COL; x++) {
            for (int y = 0; y < ROW; y++) {
                if (cells[x][y].type.equals("explored") || cells[x][y].type.equals("robot")) {
                    cells[x][y].setType("unexplored");

                }
            }
        }
        startCoord = new int[]{3, 3};
        prevCoord = new int[]{3, 3};
        setCurCoord(3, 3, "N");

        //mapDrawn = false;
        //Bitmap arrowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_baseline_clear_24);
        //obstacleDirectionBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_baseline_clear_24);

        setEditDirectionObstacleStatus = false;
        setEditStartCoordStatus = false;

        showLog("Exiting resetRobot");
        this.invalidate();
    }

    public void updateMap() {
        showLog("Entering updateMap");

        final String target = "PC";
        String message;
        int[] startCoord = this.getStartCoord();

        if (startCoord[0] != -1) {
            // Start Coord
            message = target + "," + "Startpoint" + "," + (startCoord[0] - 1) + "," + (startCoord[1] - 1) + "," + getRobotDirection().toLowerCase();
            BluetoothUtils.write(message.getBytes());
            delay();
        }

        // All Directional Obstacle
        for (int i = 0; i < obstacleDirectionCoord.size(); i++) {
            int obstacleDirectionCoordCol = 0;
            int obstacleDirectionCoordRow = 0;
            try {
                obstacleDirectionCoordCol = Integer.parseInt(obstacleDirectionCoord.get(i)[0]);
                obstacleDirectionCoordRow = Integer.parseInt(obstacleDirectionCoord.get(i)[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            message = target + "," + "Obstacle" + "," + (obstacleDirectionCoordCol - 1) + "," + (obstacleDirectionCoordRow - 1) + "," + obstacleDirectionCoord.get(i)[2].toLowerCase();
            BluetoothUtils.write(message.getBytes());
            delay();
        }

        showLog("Exiting updateMap");
    }

    private void delay() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
