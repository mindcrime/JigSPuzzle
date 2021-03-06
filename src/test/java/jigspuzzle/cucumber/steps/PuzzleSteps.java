package jigspuzzle.cucumber.steps;

import cucumber.api.PendingException;
import cucumber.api.java.en.*;
import java.awt.Container;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import jigspuzzle.controller.PuzzleController;
import jigspuzzle.controller.SettingsController;
import jigspuzzle.cucumber.RunCucumber;
import jigspuzzle.view.desktop.puzzle.PuzzlepieceView;
import org.assertj.core.api.Assertions;
import org.assertj.swing.exception.ComponentLookupException;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JPanelFixture;
import org.assertj.swing.timing.Condition;
import org.assertj.swing.timing.Pause;

/**
 * Step definitions for the input with a puzzle and the puzzle window.
 *
 * @author RoseTec
 */
public class PuzzleSteps {

    private final WindowsSteps windowsSteps;

    private int puzzleRows, puzzleColumns;
    private int[][] initX, initY;

    public PuzzleSteps(WindowsSteps windowsSteps) {
        this.windowsSteps = windowsSteps;
    }

    // creation of a puzzle
    private void createPuzzle(File imageFile) {
        get_puzzle_window().menuItem("puzzle-create").click();
        get_puzzle_window().fileChooser().setCurrentDirectory(imageFile.getParentFile()).selectFile(imageFile).approve();

        // wait until the puzzle is created
        Pause.pause(new Condition("Puzzle is created") {
            @Override
            public boolean test() {
                try {
                    return PuzzleController.getInstance().getPuzzlepieceRowCount() != 0;
                } catch (NullPointerException ex) {
                    return false;
                }
            }
        }, RunCucumber.TIMEOUT_UI_UPDATED);

        // save puzzle settings
        puzzleRows = PuzzleController.getInstance().getPuzzlepieceRowCount();
        puzzleColumns = PuzzleController.getInstance().getPuzzlepieceColumnCount();

        // wait some time until the puzzle is created
        Pause.pause(new Condition("Puzzlepieces are visable") {
            @Override
            public boolean test() {
                try {
                    get_puzzlepiece_group(
                            PuzzleController.getInstance().getPuzzlepieceRowCount() - 1,
                            PuzzleController.getInstance().getPuzzlepieceColumnCount() - 1
                    );
                    return true;
                } catch (ComponentLookupException ex) {
                    return false;
                }
            }
        }, RunCucumber.TIMEOUT_UI_UPDATED);
    }

    @Given("^(?:that )?I (?:have )?created a puzzle$")
    public void create_puzzle2() {
        create_puzzle();
    }

    @Given("^(?:that )?I (?:have )?created a sqaure puzzle with (\\d+) puzzlepieces out of a (big|small)? image")
    public void create_puzzle_big(int pieceNumber, String size) {
        SettingsController.getInstance().setPuzzlepieceNumber(pieceNumber);

        File imageFile;
        if ("big".equals(size)) {
            imageFile = new File("../src/test/images/test_puzzle_sqare_big.jpg");
        } else {
            imageFile = new File("../src/test/images/test_puzzle_sqare_small.jpg");
        }
        createPuzzle(imageFile);
    }

    @When("^I create a new puzzle$")
    public void create_puzzle1() {
        create_puzzle();
    }

    @When("^I save the puzzle$")
    public void save_puzzle() throws IOException {
        File file = new File("puzzle." + PuzzleController.PUZZLE_SAVES_ENDING);

        file.createNewFile();
        get_puzzle_window().menuItem("puzzle-save").click();
        get_puzzle_window().fileChooser().setCurrentDirectory(file.getParentFile()).selectFile(file).approve();
    }

    @When("^I load a puzzle$")
    public void load_puzzle() {
        File file = new File("puzzle." + PuzzleController.PUZZLE_SAVES_ENDING);

        get_puzzle_window().menuItem("puzzle-load").click();
        get_puzzle_window().fileChooser().setCurrentDirectory(file.getParentFile()).selectFile(file).approve();
    }

    @When("^I restart the puzzle$")
    public void restart_puzzle() {
        get_puzzle_window().menuItem("puzzle-restart").click();
    }

    @When("^I drag an image into the puzzlearea$")
    public void create_puzzle_drag_drop() {
        throw new PendingException();
    }

    @When("^I create a new puzzle with an image from my HDD$")
    public void create_puzzle() {
        // create puzzle
        File imageFile = new File("../src/test/images/test_puzzle.jpg");
        createPuzzle(imageFile);
    }
    // -- creation of a puzzle end

    // puzzlepieces
    @When("^I drag one puzzlepiec to the correct edge of a neighbored puzzlepiece$")
    public void drag_one_puzzlepiece_to_another() {
        PuzzlepieceView piece1 = (PuzzlepieceView) get_puzzlepiece_group(0, 0).target();
        PuzzlepieceView piece2 = (PuzzlepieceView) get_puzzlepiece_group(0, 1).target();

        // move the 1st piece away from the edges
        move_puzzlepiece_to(piece1, get_puzzle_area().getWidth() / 2, get_puzzle_area().getHeight() / 2);

        // move the other to the 1st piece
        move_puzzlepiece_to(piece2, piece1.getX() + piece1.getWidthOfThisGroup(), piece1.getY() + piece1.getHeightOfThisGroup() / 2);
    }

    @Then("^the new puzzle should have \"(\\d+)\" puzzlepieces$")
    public void puzzle_should_have_that_many_puzzlepieces(int number) {
//        int actualNumber = puzzleRows * puzzleColumns;
        int actualNumber = get_puzzle_area().getComponentCount();

        Assertions.assertThat(actualNumber).isBetween(number * 9 / 10, number * 11 / 10);
    }

    @Then("^I should be able to move puzzlepieces$")
    public void should_be_able_to_move_puzzlepieces() {
        PuzzlepieceView groupToMove = (PuzzlepieceView) get_puzzlepiece_group(0, 0).target();
        int offsetX = groupToMove.getWidth() / 2;
        int offsetY = groupToMove.getHeight() / 2;

        for (int moveToX : new int[]{150, 500}) {
            for (int moveToY : new int[]{150, 500}) {
                move_puzzlepiece_to(groupToMove, moveToX, moveToY);
                Assertions.assertThat(groupToMove.getX()).isBetween(moveToX - offsetX - 1, moveToX - offsetX + 1);
                Assertions.assertThat(groupToMove.getY()).isBetween(moveToY - offsetY - 1, moveToY - offsetY + 1);
            }
        }
    }

    @Then("^the two puzzlepieces should snap together and create a group$")
    public void the_puzzlepieces_snap_together() {
        PuzzlepieceView group = (PuzzlepieceView) get_puzzlepiece_group(0, 0).target();

        Assertions.assertThat(group.getNumberOfContainedPuzzlepieceGroups()).isEqualTo(2);
    }
    // -- puzzlepieces end

    // size of puzzlepieces
    @Then("^the size of (?:one|a)? puzzlepiece should be: width=(\\d+)(?:px)?, height=(\\d+)(?:px)?$")
    public void size_of_puzzlepiece_should_be(int width, int height) {
        int buffer = 20;
        PuzzlepieceView piece = (PuzzlepieceView) (get_puzzlepiece_group(0, 0).target());

        Assertions.assertThat(piece.getHeightOfThisGroup() / 2).isBetween(height - height / buffer, height + height / buffer); // ' / 2' because of the connector-size.
        Assertions.assertThat(piece.getWidthOfThisGroup() / 2).isBetween(width - width / buffer, width + width / buffer);
    }
    // -- size of puzzlepieces end

    @Then("^I should see the complete puzzle$")
    public void see_complete_puzzle() {
        // yeah, the step says something different than the implementation here....
        get_puzzlepiece_group(0, 0).requireEnabled(); // check (0,0) at least once
        for (int x = 0; x < puzzleRows; x++) {
            for (int y = 0; y < puzzleColumns; y++) {
                get_puzzlepiece_group(x, y).requireEnabled();
            }
        }
    }

    @Then("^I should see the restarted puzzle$")
    public void see_restarted_puzzle() {
        see_complete_puzzle();
    }

    @Then("^I should see a puzzle of that image$")
    public void see_puzzle() {
        see_complete_puzzle();
    }

    @Then("^the puzzle should scatter all over the puzzlearea$")
    public void puzzle_should_scatter() {
        throw new PendingException();
    }

    private JPanelFixture get_puzzlepiece_group(int x, int y) {
        return get_puzzle_window().panel("puzzlepiece-group-" + x + "-" + y);
    }

    private Container get_puzzle_area() {
        return (Container) get_puzzle_window().panel("puzzlearea-panel").target().getComponent(0);
    }

    private FrameFixture get_puzzle_window() {
        return (FrameFixture) windowsSteps.getPuzzleWindow();
    }

    public void move_puzzlepiece_to(PuzzlepieceView puzzlepiece, int x, int y) {
        windowsSteps.getRobot().pressMouse(puzzlepiece, new Point(puzzlepiece.getWidth() / 2, puzzlepiece.getHeight() / 2));
        windowsSteps.getRobot().moveMouse(get_puzzle_area(), x, y);
        windowsSteps.getRobot().releaseMouseButtons();
    }

}
