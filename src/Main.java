import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Created by Karlson on 07.12.2015.
 */
public class Main extends Application {
    int cell_size = 40;                                                   //determines every cell size
    int hor_cells = 10;                                                   //determines columns number
    int vert_cells = 10;                                                  //determines raws number

    Cell[][] field = new Cell[hor_cells][vert_cells];                     //gamefield

    int[] neighbors = {-1, -1, -1, 0, -1, 1, 0, -1, 0, 1, 1, -1, 1, 0, 1, 1};       //relative coordinates for
                                                                                    //neighbor cells

    Label game_over_label = new Label();                                    //"Game Over" message

    int bombs_count = 0;                                                  //planted bombs counter
    int flag_count = 0;                                                   //planted flags counter

    Label flags = new Label();                                            //message with flags/bombs numbers

    int width = cell_size * hor_cells;                                    //width of minefield
    int height = cell_size * vert_cells;                                  //height of minefield

    double bomb_chance = 0.12;                                            //chance that bomb will be on cell

    private Parent createField() {                                        //creates game window
        Pane root = new Pane();

        root.getChildren().add(game_over_label);                            //prepears "Game Over" message
        game_over_label.setVisible(false);
        game_over_label.setPrefSize(300, 50);
        game_over_label.setFont(Font.font(24));
        game_over_label.setTranslateX(width / 2 - 150);
        game_over_label.setTranslateY(height + 50);
        game_over_label.setAlignment(Pos.CENTER);

        root.getChildren().add(flags);                                    //adds planted flags counter on window
        flags.setFont(Font.font(24));
        flags.setTranslateX(10);
        flags.setTranslateY(15);
        flags.setAlignment(Pos.CENTER);

        root.setPrefSize(width, height + 100);                            //determines size of game window


        for (int y = 0; y < vert_cells; y++) {                            //fills field with cells
            for (int x = 0; x < hor_cells; x++) {
                Cell cell = new Cell(x, y, Math.random() < bomb_chance);            //puts a bomb on cell
                if (cell.has_bomb){                                                 //with some chance
                    bombs_count += 1;                                               //and counts planted bombs
                    flags_reload();
                }

                field[x][y] = cell;
                root.getChildren().add(cell);
            }
        }



        for (int y = 0; y < vert_cells; y++) {                              //counts how many bombs are in
            for (int x = 0; x < hor_cells; x++) {                           //neighbor cells
                Cell cell = field[x][y];                                    //and puts this number as text value
                                                                            //for cell
                if (!cell.has_bomb) {

                //      -1,-1  0,-1  1,-1           relative coordinates for all neighbor cells
                //      -1, 0   X    1, 0
                //      -1, 1  0, 1  1, 1

                    int bombs_count = 0;

                    for (int i = 0; i < 8; i++) {
                        int neighborX = cell.x + neighbors[i * 2];

                        int neighborY = cell.y + neighbors[i * 2 + 1];


                        if (is_in_field(neighborX, neighborY)) {                  //controls if cell with these
                            Cell neighborCell = field[neighborX][neighborY];    //coordinates exists

                            if (neighborCell.has_bomb) {
                                bombs_count += 1;
                            }
                        }
                    }
                    if (bombs_count == 0){
                        cell.text.setText("");
                    } else {
                        cell.text.setText(String.valueOf(bombs_count));


                        switch (bombs_count) {                     //puts color for every number
                            case 1:
                                cell.text.setFill(Color.NAVY);
                                break;
                            case 2:
                                cell.text.setFill(Color.OLIVEDRAB);
                                break;
                            case 3:
                                cell.text.setFill(Color.DARKORANGE);
                                break;
                            case 4:
                                cell.text.setFill(Color.DARKRED);
                                break;
                            case 5:
                                cell.text.setFill(Color.YELLOW);
                                break;
                            case 6:
                                cell.text.setFill(Color.DARKVIOLET);
                                break;
                            case 7:
                                cell.text.setFill(Color.INDIGO);
                                break;
                            case 8:
                                cell.text.setFill(Color.DARKGREEN);
                                break;
                        }
                    }
                }
            }
        }

        return root;
    }

    private boolean is_in_field(int x, int y) {                     //checks, if those coordinates are on the field
        if (x >= 0 && x < hor_cells && y >= 0 && y < vert_cells){
            return true;
        } else {
            return false;
        }
    }



    public class Cell extends StackPane{                           //determines Cell class
        private  int x, y;                                         //every cell on the field is object of this class
        private boolean has_bomb;
        private boolean is_opened = false;
        private boolean is_flaged = false;

        private Rectangle border = new Rectangle(cell_size - 2, cell_size -2);
        private Text text = new Text();

        public Cell(int x, int y, boolean has_bomb){                //constructor method for the Cell class
            this.x = x;
            this.y = y;
            this.has_bomb = has_bomb;

            border.setStroke(Color.IVORY);                         //makes grid on the field
            border.setFill(Color.GREY);

            text.setFont(Font.font(24));                           //sets bomb sign
            if (has_bomb) {
                text.setText("X");
                text.setFill(Color.CRIMSON);
            } else {
                text.setText("");                                   //sets empty cell
            }
            text.setVisible(false);


            getChildren().addAll(border, text);

            setTranslateX(x * cell_size);                           //puts cells on the field
            setTranslateY(y * cell_size + 50);

            setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY){
                    open(this);                                     //opens cell on left click
                } else if (event.getButton() == MouseButton.SECONDARY){
                    set_flag(this);                                     //flags cell on right click
                }
            });
        }
    }

    private void set_flag(Cell c) {                                     //plants flag over bomb
        if (!c.is_opened){
            if (!c.is_flaged){
                c.border.setFill(Color.DARKRED);
                c.is_flaged = true;
                flag_count += 1;                            //shows 1 flag more on the flag counter
                flags_reload();
            } else{
                c.border.setFill(Color.GREY);                       //if flag is planted, removes it
                c.is_flaged = false;
                flag_count -= 1;                            //shows 1 flag less on the flag counter
                flags_reload();
            }
        }
    }

    private void flags_reload() {
        flags.setText("Mines found: "+flag_count+"/"+ bombs_count);  //shows how many flags are planted
    }


    private void open(Cell c) {
        if (c.is_opened) {                                          //exit from recursive method
            return;
        }

        if (c.has_bomb){                                            //if bomb is opened, shows "defeat" message
            c.text.setFont(Font.font(24));
            c.text.setFill(Color.BLACK);
            c.text.setVisible(true);
            c.border.setFill(Color.DARKRED);
            gameOver(false);
            return;
        }

        c.is_opened = true;                                         //opens cell
        c.text.setVisible(true);
        c.border.setFill(Color.LIGHTGREY);
        if (c.is_flaged){                                           //if empty flaged cell is opened,
            c.is_flaged = false;                                    //corrects flags count
            flag_count -= 1;
            flags_reload();
        }

        if (c.text.getText().isEmpty()) {                           //if there aren't any bombs in neighborhood,
            for (int i = 0; i < 8; i++) {                           //opens all neighbor cells
                int neighborX = c.x + neighbors[i * 2];
                int neighborY = c.y + neighbors[i * 2 + 1];


                if (is_in_field(neighborX, neighborY)) {
                    Cell neighborCell = field[neighborX][neighborY];
                    open(neighborCell);
                }
            }
        }

        if (is_field_opened()){                                       //if all empty cells are opened, shows
            gameOver(true);                                           //"victory" message
        }
    }

    private boolean is_field_opened() {                               //checks, is there any closed empty cells
        for (int y = 0; y < vert_cells; y++) {
            for (int x = 0; x < hor_cells; x++) {
                Cell cell = field[x][y];
                if (!cell.is_opened && !cell.has_bomb){
                    return false;
                }
            }
        }
        return true;
    }

    private void gameOver(boolean win) {                                //Shows "victory" or "defeat" message
        if (win){
            game_over_label.setText("You won!!!");
            flag_count = bombs_count;
            flags_reload();
        } else {
            game_over_label.setText("Sorry, You lost...");
        }
        game_over_label.setVisible(true);

        for (int y = 0; y < vert_cells; y++) {
            for (int x = 0; x < hor_cells; x++) {                       //opens whole field
                Cell cell = field[x][y];
                cell.text.setVisible(true);
            }
        }

    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Minesweeper");
        Scene scene = new Scene(createField());

        primaryStage.setScene(scene);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
