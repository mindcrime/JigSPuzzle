package jigspuzzle.controller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import jigspuzzle.JigSPuzzle;
import jigspuzzle.model.settings.LanguageSettings;
import jigspuzzle.model.settings.PuzzleSettings;
import jigspuzzle.model.settings.PuzzleareaSettings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * A class for handeling all actions for settings.
 *
 * @author RoseTec
 */
public class SettingsController extends AbstractController {

    private static SettingsController instance;

    public static SettingsController getInstance() {
        if (instance == null) {
            instance = new SettingsController();
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetInstance() {
        instance = null;
    }

    /**
     * The settings for the current language.
     */
    private LanguageSettings languageSettings;

    /**
     * The settings for the puzzle itself.
     */
    private PuzzleSettings puzzleSettings;

    /**
     * The settings for the puzzlearea.
     */
    private PuzzleareaSettings puzzleareaSettings;

    /**
     * The filename for storing the settings in a file.
     */
    public final static String SETTINGS_FILE_NAME = "settings.xml";

    private SettingsController() {
        puzzleSettings = new PuzzleSettings();
        puzzleareaSettings = new PuzzleareaSettings();
        languageSettings = new LanguageSettings();

        try {
            loadSettingsFromFile();
        } catch (IOException ex) {
        }
    }

    /**
     * Loads the stored settings from the settings-file. The file was previously
     * created, when saving the settings.
     *
     * If the file is not availible, an IOException is thrown.
     *
     * @throws java.io.IOException
     */
    public void loadSettingsFromFile() throws IOException {
        File file = new File(SETTINGS_FILE_NAME);

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            doc.getDocumentElement().normalize();

            Node settingsNode = doc.getElementsByTagName("settings").item(0);
            if (settingsNode != null) {
                languageSettings.loadFromFile((Element) settingsNode);
                puzzleSettings.loadFromFile((Element) settingsNode);
                puzzleareaSettings.loadFromFile((Element) settingsNode);
            }
        } catch (SAXException | ParserConfigurationException ex) {
        }
    }

    /**
     * Saves the current settings into the settings-file.
     *
     * If the something wents wrong while saving, an IOException is thrown.
     *
     * @throws IOException
     */
    public void saveSettingsToFile() throws IOException {
        File file = new File(SETTINGS_FILE_NAME);

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            doc.appendChild(doc.createComment("This file is created, when saving the settings of JigSPuzzle."));
            Element root = doc.createElement("settings");
            doc.appendChild(root);
            languageSettings.saveToFile(doc, root);
            puzzleSettings.saveToFile(doc, root);
            puzzleareaSettings.saveToFile(doc, root);

            // write the content into xml file
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);

            transformer.transform(source, result);
        } catch (ParserConfigurationException ex) {
        } catch (TransformerConfigurationException ex) {
        } catch (TransformerException ex) {
        }
    }

    /**
     * Gets all available languages, that can be used for the user interface.
     *
     * @return
     */
    public String[] getAvailableLanguages() {
        return languageSettings.getAvailableLanguages();
    }

    /**
     * Gets the current language.
     *
     * @return
     */
    public String getCurrentLanguage() {
        return languageSettings.getCurrentLanguage();
    }

    /**
     * Sets the new current language. If the given language is not available,
     * nothing happens.
     *
     * @param newLanguage
     */
    public void setCurrentLanguage(String newLanguage) {
        languageSettings.setCurrentLanguage(newLanguage);
    }

    /**
     * Gets the value for automatically decrease the size of the puzzle, if it
     * does not fit into the puzzleare, which displays the puzzle.
     *
     * @return
     * @see #getPuzzlepieceSize(int, int)
     */
    public boolean getDecreasePuzzleAutomatically() {
        return puzzleareaSettings.getDecreasePuzzleAutomatically();
    }

    /**
     * Sets the value for automatically decrease the size of the puzzle, if it
     * does not fit into the puzzleare, which displays the puzzle.
     *
     * @param value
     * @see #getPuzzlepieceSize(int, int)
     */
    public void setDecreasePuzzleAutomatically(boolean value) {
        puzzleareaSettings.setDecreasePuzzleAutomatically(value);
    }

    /**
     * Gets the value for automatically enlarge the size of the puzzle, if it
     * does not fit into the puzzleare, which displays the puzzle.
     *
     * @return
     * @see #getPuzzlepieceSize(int, int)
     */
    public boolean getEnlargePuzzleAutomatically() {
        return puzzleareaSettings.getEnlargePuzzleAutomatically();
    }

    /**
     * Sets the value for automatically enlarge the size of the puzzle, if it
     * does not fit into the puzzleare, which displays the puzzle.
     *
     * @param value
     * @see #getPuzzlepieceSize(int, int)
     */
    public void setEnlargePuzzleAutomatically(boolean value) {
        puzzleareaSettings.setEnlargePuzzleAutomatically(value);
    }

    /**
     * Gets the text in the current language for the given pageId and textId.
     *
     * If no text in the current language is given in the file for the language,
     * then the english text is returned. If also the english text is not
     * availibe, then the text <code>readText-[pageId]-[textId]</code> is
     * returned.
     *
     * @param pageId
     * @param textId
     * @return
     */
    public String getLanguageText(int pageId, int textId) {
        return getLanguageText(pageId, textId, null);
    }

    /**
     * Gets the text in the current language for the given pageId and textId.
     *
     * If no text in the current language is given in the file for the language,
     * then the english text is returned. If also the english text is not
     * availibe, then the text <code>readText-[pageId]-[textId]</code> is
     * returned.
     *
     * @param pageId
     * @param textId
     * @param variableMapping A mapping for variables used in the text for the
     * language. It map the variable to a given value.
     *
     * Variables are used in the text as follows:
     * <code>text {{variable}} text</code>.
     * @return
     */
    public String getLanguageText(int pageId, int textId, Map<String, String> variableMapping) {
        return languageSettings.getText(pageId, textId, variableMapping);
    }

    /**
     * @see PuzzleareaSettings#getMonitorForFullscreen()
     */
    public List<Integer> getMonitorsForFullscreen() {
        return puzzleareaSettings.getMonitorForFullscreen();
    }

    /**
     * @see PuzzleareaSettings#setMonitorForFullscreen(java.util.List)
     */
    public void setMonitorForFullscreen(List<Integer> monitorForFullscreen) {
        puzzleareaSettings.setMonitorForFullscreen(monitorForFullscreen);
    }

    /**
     * @see PuzzleareaSettings#getPlaySounds()
     * @return
     */
    public boolean getPlaySounds() {
        return puzzleareaSettings.getPlaySounds();
    }

    /**
     * @see PuzzleareaSettings#setPlaySounds(boolean)
     * @param playSounds
     */
    public void setPlaySounds(boolean playSounds) {
        puzzleareaSettings.setPlaySounds(playSounds);
    }

    /**
     * Gets the bckground color for the puzzlearea where the player playes
     * around with the puzzlepieces.
     *
     * @return
     */
    public Color getPuzzleareaBackgroundColor() {
        return puzzleareaSettings.getPuzzleareaBackgroundColor();
    }

    /**
     * Sets the bckground color for the puzzlearea where the player playes
     * around with the puzzlepieces.
     *
     * @param newColor
     */
    public void setPuzzleareaBackgroundColor(Color newColor) {
        puzzleareaSettings.setPuzzleareaBackgroundColor(newColor);
    }

    /**
     * @return @see PuzzleSettings#getPuzzlepieceConnectorShapeId()
     */
    public int getPuzzlepieceConnectorShapeId() {
        return puzzleSettings.getPuzzlepieceConnectorShapeId();
    }

    /**
     * @param puzzlepieceConnectorShapeId
     * @see PuzzleSettings#setPuzzlepieceConnectorShapeId(int)
     */
    public void setPuzzlepieceConnectorShapeId(int puzzlepieceConnectorShapeId) {
        puzzleSettings.setPuzzlepieceConnectorShapeId(puzzlepieceConnectorShapeId);
    }

    /**
     * Gets the numbr of puzzlepieces that a new puzzle should have.
     *
     * @return
     */
    public int getPuzzlepieceNumber() {
        return puzzleSettings.getPuzzlepieceNumber();
    }

    /**
     * Sets the numbr of puzzlepieces that a new puzzle should have.
     *
     * @param newNumber
     */
    public void setPuzzlepieceNumber(int newNumber) {
        puzzleSettings.setPuzzlepieceNumber(newNumber);
    }

    /**
     * Returns the height and width that one puzzlepiece should have, with the
     * given size of the puzzlearea. The settings for modifiing the size of a
     * puzzlepiece is considered in here.
     *
     * @return
     */
    public Dimension getPuzzlepieceSize() {
        return getPuzzlepieceSize(
                PuzzleController.getInstance().getPuzzleHeight(),
                PuzzleController.getInstance().getPuzzleWidth(),
                PuzzleController.getInstance().getPuzzlepieceRowCount(),
                PuzzleController.getInstance().getPuzzlepieceColumnCount());
    }

    /**
     * Returns the height and width that one puzzlepiece should have, with the
     * given size of the puzzlearea. The settings for modifiing the size of a
     * puzzlepiece is considered in here.
     *
     * @param puzzleHeight
     * @param puzzleWidth
     * @param puzzleRows
     * @param puzzleColumns
     * @return
     * @see #getPuzzlepieceSize(int, int)
     */
    Dimension getPuzzlepieceSize(double puzzleHeight, double puzzleWidth, int puzzleRows, int puzzleColumns) {
        int maxHeight = 0, maxWidth = 0;

        for (Rectangle screen : JigSPuzzle.getInstance().getPuzzleWindow().getPuzzleareaBounds()) {
            int puzzleareaWidth = screen.width;
            int puzzleareaHeight = screen.height;
            // resize puzzlearea depending on setting for size of puzzlearea
            puzzleareaWidth *= puzzleareaSettings.getUsedSizeOfPuzzlearea();
            puzzleareaHeight *= puzzleareaSettings.getUsedSizeOfPuzzlearea();

            // Gets the dimension, that restricts the puzzlesie more
            boolean topRestricsMoreThanLeft;

            topRestricsMoreThanLeft = (puzzleHeight / puzzleareaHeight > puzzleWidth / puzzleareaWidth);

            // resize the puzzlepiece-size depending of the size of puzzlearea
            int resizedHeight = (int) (puzzleHeight);
            int resizedWidth = (int) (puzzleWidth);

            if (puzzleareaSettings.getDecreasePuzzleAutomatically()
                    && (puzzleareaHeight < puzzleHeight || puzzleareaWidth < puzzleWidth)) {
                if (topRestricsMoreThanLeft && puzzleareaHeight < puzzleHeight) {
                    resizedHeight = puzzleareaHeight;
                    resizedWidth = (int) (resizedHeight * puzzleWidth / puzzleHeight);
                } else if (!topRestricsMoreThanLeft && puzzleareaWidth < puzzleWidth) {
                    resizedWidth = puzzleareaWidth;
                    resizedHeight = (int) (resizedWidth * puzzleHeight / puzzleWidth);
                }
            } else if (puzzleareaSettings.getEnlargePuzzleAutomatically()
                    && (puzzleareaHeight > puzzleHeight || puzzleareaWidth > puzzleWidth)) {
                if (topRestricsMoreThanLeft && puzzleareaHeight > puzzleHeight) {
                    resizedHeight = puzzleareaHeight;
                    resizedWidth = (int) (resizedHeight * puzzleWidth / puzzleHeight);
                } else if (!topRestricsMoreThanLeft && puzzleareaWidth > puzzleWidth) {
                    resizedWidth = puzzleareaWidth;
                    resizedHeight = (int) (resizedWidth * puzzleHeight / puzzleWidth);
                }
            }

            // set this size to the current, if it is greater than the current
            if (resizedHeight * resizedWidth > maxHeight * maxWidth) {
                maxHeight = resizedHeight;
                maxWidth = resizedWidth;
            }
        }

        return new Dimension(maxWidth / puzzleColumns, maxHeight / puzzleRows);
    }

    /**
     * @see PuzzleSettings#setSnapDistancePercent(int)
     * @param snapDistancePercent
     */
    public void setPuzzlepieceSnapDistancePercent(int snapDistancePercent) {
        puzzleSettings.setSnapDistancePercent(snapDistancePercent);
    }

    /**
     * @return @see PuzzleSettings#getSnapDistancePercent
     */
    public int getPuzzlepieceSnapDistancePercent() {
        return puzzleSettings.getSnapDistancePercent();
    }

    /**
     * @see PuzzleareaSettings#getShowPuzzlePreview()
     * @return
     */
    public boolean getShowPuzzlePreview() {
        return puzzleareaSettings.getShowPuzzlePreview();
    }

    /**
     * @see PuzzleareaSettings#setShowPuzzlePreview(boolean)
     * @param showPuzzlePreview
     */
    public void setShowPuzzlePreview(boolean showPuzzlePreview) {
        puzzleareaSettings.setShowPuzzlePreview(showPuzzlePreview);
    }

    /**
     * Sets the number in percent, how much of the puzzleare should be used for
     * puzzeling. A number of 0.5 for instance means, that only 50% of the
     * puzzelarea should be used for the final puzzle.
     *
     * @param number
     * @see #getPuzzlepieceSize(int, int)
     */
    public void setUsedSizeOfPuzzlearea(double number) {
        puzzleareaSettings.setUsedSizeOfPuzzlearea(number);
    }

    /**
     * Gets the number in percent, how much of the puzzleare should be used for
     * puzzeling. A number of 0.5 for instance means, that only 50% of the
     * puzzelarea should be used for the final puzzle.
     *
     * @return
     * @see #getPuzzlepieceSize(int, int)
     */
    public double getUsedSizeOfPuzzlearea() {
        return puzzleareaSettings.getUsedSizeOfPuzzlearea();
    }

    /**
     * @return @see PuzzleSettings#getUseRandomConnectorShape()
     */
    public boolean getUseRandomConnectorShape() {
        return puzzleSettings.getUseRandomConnectorShape();
    }

    /**
     * @param useRandomConnectorShape
     * @see PuzzleSettings#setUseRandomConnectorShape(boolean)
     */
    public void setUseRandomConnectorShape(boolean useRandomConnectorShape) {
        puzzleSettings.setUseRandomConnectorShape(useRandomConnectorShape);
    }

    /**
     * Adds an observer for the LanguageSettings
     *
     * @param o
     * @see Observable#addObserver(java.util.Observer)
     * @see PuzzleSettings
     */
    public synchronized void addLanguageSettingsObserver(Observer o) {
        languageSettings.addObserver(o);
    }

    /**
     * Delets an observer for the LanguageSettings
     *
     * @param o
     * @see Observable#deleteObserver(java.util.Observer)
     * @see PuzzleSettings
     */
    public synchronized void deleteLanguageSettingsObserver(Observer o) {
        languageSettings.deleteObserver(o);
    }

    /**
     * Adds an observer for the PuzzleSettings
     *
     * @param o
     * @see Observable#addObserver(java.util.Observer)
     * @see PuzzleSettings
     */
    public synchronized void addPuzzleSettingsObserver(Observer o) {
        puzzleSettings.addObserver(o);
    }

    /**
     * Delets an observer for the PuzzleSettings
     *
     * @param o
     * @see Observable#deleteObserver(java.util.Observer)
     * @see PuzzleSettings
     */
    public synchronized void deletePuzzleSettingsObserver(Observer o) {
        puzzleSettings.deleteObserver(o);
    }

    /**
     * Adds an observer for the PuzzleAreaSettings
     *
     * @param o
     * @see Observable#addObserver(java.util.Observer)
     * @see PuzzleareaSettings
     */
    public synchronized void addPuzzleareaSettingsObserver(Observer o) {
        puzzleareaSettings.addObserver(o);
    }

    /**
     * Delets an observer for the PuzzleAreaSettings
     *
     * @param o
     * @see Observable#deleteObserver(java.util.Observer)
     * @see PuzzleareaSettings
     */
    public synchronized void deletePuzzleareaSettingsObserver(Observer o) {
        puzzleareaSettings.deleteObserver(o);
    }

}
