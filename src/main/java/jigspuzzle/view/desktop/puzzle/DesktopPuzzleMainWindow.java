package jigspuzzle.view.desktop.puzzle;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Observable;
import javax.swing.JComponent;
import javax.swing.filechooser.FileNameExtensionFilter;
import jigspuzzle.JigSPuzzle;
import jigspuzzle.controller.PuzzleController;
import jigspuzzle.controller.SettingsController;
import jigspuzzle.model.puzzle.Puzzle;
import jigspuzzle.model.puzzle.PuzzlepieceGroup;
import jigspuzzle.model.settings.PuzzleareaSettings;
import jigspuzzle.util.ImageUtil;
import jigspuzzle.view.IPuzzleWindow;
import jigspuzzle.view.ImageGetter;
import jigspuzzle.view.desktop.swing.ErrorMessageDialog;
import jigspuzzle.view.desktop.swing.HideableJMenuBar;
import jigspuzzle.view.desktop.swing.JFileChooser;
import jigspuzzle.view.desktop.swing.JMenu;
import jigspuzzle.view.desktop.swing.JMenuItem;
import jigspuzzle.view.desktop.swing.ThumbnailView;

/**
 *
 * @author RoseTec
 */
public class DesktopPuzzleMainWindow extends javax.swing.JFrame {

    /**
     * the area, where the user can play with puzzlepieces
     */
    private final Puzzlearea puzzlearea;

    /**
     * The class that contains all references to the windows for the desktop
     * version.
     */
    private final DesktopPuzzleWindow desktopPuzzleWindow;

    /**
     * The file to that the last time the puzzle was saved to. If this is set,
     * there will be no dialog to select the file to save to when saving.
     *
     * The file is set to <code>null</code> when a new puzzle is created.
     */
    private File lastSavedFile = null;

    /**
     * The size of the icons of the menu items.
     */
    private Dimension menuItemIconSize = new Dimension(20, 20);

    /**
     * Creates new form PuzzleWindow
     *
     * @param desktopPuzzleWindow
     */
    public DesktopPuzzleMainWindow(DesktopPuzzleWindow desktopPuzzleWindow) {
        this.desktopPuzzleWindow = desktopPuzzleWindow;
        initComponents();

        jMenuItem1.setIcon(ImageUtil.transformImageToIcon(ImageGetter.getInstance().getNewPuzzleImage(), menuItemIconSize));
        jMenuItem2.setIcon(ImageUtil.transformImageToIcon(ImageGetter.getInstance().getLoadImage(), menuItemIconSize));
        jMenuItem3.setIcon(ImageUtil.transformImageToIcon(ImageGetter.getInstance().getSaveImage(), menuItemIconSize));
        jMenuItem4.setIcon(ImageUtil.transformImageToIcon(ImageGetter.getInstance().getShuffleImage(), menuItemIconSize));
        jMenuItem5.setIcon(ImageUtil.transformImageToIcon(ImageGetter.getInstance().getRestartImage(), menuItemIconSize));
        jMenuItem6.setIcon(ImageUtil.transformImageToIcon(ImageGetter.getInstance().getExitImage(), menuItemIconSize));
//        jMenuItem7.setIcon(ImageUtil.transformImageToIcon(ImageGetter.getInstance().getSettingsImage(), menuItemIconSize));
        jMenuItem8.setIcon(ImageUtil.transformImageToIcon(ImageGetter.getInstance().getSettingsImage(), menuItemIconSize));
        jMenuItem11.setIcon(ImageUtil.transformImageToIcon(ImageGetter.getInstance().getFullscreenImage(), menuItemIconSize));
        jMenuItem9.setIcon(ImageUtil.transformImageToIcon(ImageGetter.getInstance().getInfoImage(), menuItemIconSize));

        // create Puzzlearea
        puzzlearea = new Puzzlearea();

        jPanel1.add(puzzlearea);
        setPuzzleareaStart(new Point(0, 0));

        // load language texts
        SettingsController.getInstance().addLanguageSettingsObserver((Observable o, Object arg) -> {
            loadLanguageTexts();
        });
        loadLanguageTexts();

        // register observer for setting the background color in the main window for live preview
        SettingsController.getInstance().addPuzzleareaSettingsObserver((Observable o, Object arg) -> {
            PuzzleareaSettings settings = (PuzzleareaSettings) o;
            DesktopPuzzleMainWindow.this.jPanel1.setBackground(settings.getPuzzleareaBackgroundColor());
        });
        this.getContentPane().setBackground(SettingsController.getInstance().getPuzzleareaBackgroundColor());

        // if in fullscreen mode ESC is preesed, fullscreen is exited
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE && desktopPuzzleWindow.isFullscreenActive()) {
                    desktopPuzzleWindow.triggerFullscreen();
                }
            }
        });

        // deactivate buttons when no puzzle is there
        onPuzzleClosed();
    }

    /**
     * @see IPuzzleWindow#bringToFront(jigspuzzle.model.puzzle.PuzzlepieceGroup)
     */
    public void bringToFront(PuzzlepieceGroup puzzlepieceGroup) {
        puzzlearea.bringToFront(puzzlepieceGroup);
    }

    /**
     * @see IPuzzleWindow#getPuzzleareaBounds()
     */
    public Rectangle getPuzzleareaBounds() {
        if (desktopPuzzleWindow.isFullscreenActive()) {
            return new Rectangle(this.getLocation(), puzzlearea.getSize());
        } else {
            return new Rectangle(puzzlearea.getSize());
        }
    }

    /**
     * @see Puzzlearea#setPuzzleareaStart(java.awt.Point)
     */
    void setPuzzleareaStart(Point p) {
        puzzlearea.setPuzzleareaStart(p);
    }

    /**
     * @see IPuzzleWindow#getPuzzlepieceHeight()
     */
    public int getPuzzlepieceHeight() {
        return puzzlearea.getPuzzlepieceHeight();
    }

    /**
     * @see IPuzzleWindow#getPuzzlepieceWidth()
     */
    public int getPuzzlepieceWidth() {
        return puzzlearea.getPuzzlepieceWidth();
    }

    /**
     * @see IPuzzleWindow#showPuzzleWindow()
     */
    public void showPuzzleWindow() {
        this.setVisible(true);
    }

    /**
     * @see IPuzzleWindow#setNewPuzzle(jigspuzzle.model.puzzle.Puzzle)
     */
    public void setNewPuzzle(Puzzle puzzle) {
        lastSavedFile = null;
        puzzlearea.setNewPuzzle(puzzle);

        jMenuItem3.setEnabled(true);
        jMenuItem4.setEnabled(true);
        jMenuItem5.setEnabled(true);
    }

    /**
     * Loads all texts from the settings in the current language.
     */
    private void loadLanguageTexts() {
        int textId;

        this.setTitle(SettingsController.getInstance().getLanguageText(1, 1));
        jMenu1.setText(SettingsController.getInstance().getLanguageText(1, 100));
        jMenuItem1.setText(SettingsController.getInstance().getLanguageText(1, 110));
        jMenuItem2.setText(SettingsController.getInstance().getLanguageText(1, 120));
        jMenuItem3.setText(SettingsController.getInstance().getLanguageText(1, 130));
        jMenuItem4.setText(SettingsController.getInstance().getLanguageText(1, 150));
        jMenuItem5.setText(SettingsController.getInstance().getLanguageText(1, 160));
        jMenuItem6.setText(SettingsController.getInstance().getLanguageText(1, 180));

        jMenu2.setText(SettingsController.getInstance().getLanguageText(1, 300));
        jMenuItem8.setText(SettingsController.getInstance().getLanguageText(1, 310));
        jMenuItem7.setText(SettingsController.getInstance().getLanguageText(1, 320));

        jMenu3.setText(SettingsController.getInstance().getLanguageText(1, 500));
        jMenuItem9.setText(SettingsController.getInstance().getLanguageText(1, 510));
        jMenuItem10.setText(SettingsController.getInstance().getLanguageText(1, 520));

        jMenu4.setText(SettingsController.getInstance().getLanguageText(1, 700));
        textId = desktopPuzzleWindow.isFullscreenActive() ? 711 : 710;
        jMenuItem11.setText(SettingsController.getInstance().getLanguageText(1, textId));

        // also set the default locale of the swing components
        //TODO: move this to DesktopPuzzleWindow.java?
        JComponent.setDefaultLocale(Locale.getDefault());
    }

    /**
     * Will be call, when the fullscreen is triggered.
     */
    void fullscreenTriggered() {
        if (this.isUndecorated()) {
            // adjust window only for the jframes in fullscreen
            jMenuItem11.setIcon(ImageUtil.transformImageToIcon(ImageGetter.getInstance().getFullscreenCloseImage(), menuItemIconSize));

            jPanel1.setLayout(null);
            puzzlearea.setSize(getSize());

            // show the menu on fullscreen only when the mouse is on top of the screen
            ((HideableJMenuBar) jMenuBar1).setHidden(true);
            this.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    //todo: This depends on a new object each time the fullscreen is activated
                    if (!desktopPuzzleWindow.isFullscreenActive()) {
                        return;
                    }
                    int borderYBecomeVisible = 3;
                    int borderYBecomeUnvisible = 50;
                    int yOnPuzzlearea = e.getLocationOnScreen().y - DesktopPuzzleMainWindow.this.getBounds().y;

                    if (yOnPuzzlearea > borderYBecomeUnvisible && !((HideableJMenuBar) jMenuBar1).isHidden()) {
                        ((HideableJMenuBar) jMenuBar1).setHidden(true);
                        puzzlearea.setLocation(0, 0);
                    } else if (yOnPuzzlearea < borderYBecomeVisible && ((HideableJMenuBar) jMenuBar1).isHidden()) {
                        int menuBarOffset;

                        ((HideableJMenuBar) jMenuBar1).setHidden(false);
                        menuBarOffset = jMenuBar1.getPreferredSize().height;
                        puzzlearea.setLocation(0, -menuBarOffset);
                    }
                }
            });
        } else {
            // trigger component resized to ensure, the puzlepieces are in the puzzlearea
            puzzlearea.dispatchEvent(new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED));
        }

        // text for fullscreen-menuItem changed
        loadLanguageTexts();
    }

    /**
     * Actions, that are executed, when the current puzzle is closed and
     * therefore removed from the puzzlearea.
     */
    private void onPuzzleClosed() {
        jMenuItem3.setEnabled(false);
        jMenuItem4.setEnabled(false);
        jMenuItem5.setEnabled(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jMenuBar1 = new HideableJMenuBar();
        jMenu1 = new JMenu();
        jMenuItem1 = new JMenuItem();
        jMenuItem2 = new JMenuItem();
        jMenuItem3 = new JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem4 = new JMenuItem();
        jMenuItem5 = new JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem6 = new JMenuItem();
        jMenu2 = new JMenu();
        jMenuItem8 = new JMenuItem();
        jMenuItem7 = new JMenuItem();
        jMenu4 = new JMenu();
        jMenuItem11 = new JMenuItem();
        jMenu3 = new JMenu();
        jMenuItem9 = new JMenuItem();
        jMenuItem10 = new JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("JigSPuzzle");
        setIconImage(ImageGetter.getInstance().getJigSPuzzleImage());
        setPreferredSize(new java.awt.Dimension(1200, 900));

        jPanel1.setName("puzzlearea-panel"); // NOI18N
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));
        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jMenuBar1.setName("main-menu"); // NOI18N

        jMenu1.setText("Puzzle");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Neues Puzzle Erstellen");
        jMenuItem1.setName("puzzle-create"); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText("Altes Puzzle Laden");
        jMenuItem2.setName("puzzle-load"); // NOI18N
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem3.setText("Dieses Puzzle Speichern");
        jMenuItem3.setName("puzzle-save"); // NOI18N
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);
        jMenu1.add(jSeparator1);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem4.setText("Puzzle Durchmischen");
        jMenuItem4.setName("puzzle-shuffle"); // NOI18N
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem5.setText("Puzzle Neu Starten");
        jMenuItem5.setName("puzzle-restart"); // NOI18N
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);
        jMenu1.add(jSeparator2);

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem6.setText("JigSPuzzle Beenden");
        jMenuItem6.setName("exit"); // NOI18N
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem6);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Einstellungen");
        jMenu2.setName("settings"); // NOI18N

        jMenuItem8.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem8.setText("Darstellungs-Einstellungen");
        jMenuItem8.setName("appearance-settings"); // NOI18N
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem8);

        jMenuItem7.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem7.setText("Puzzle-Optionen");
        jMenuItem7.setContentAreaFilled(false);
        jMenuItem7.setName("puzzle-settings"); // NOI18N
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem7);

        jMenuBar1.add(jMenu2);

        jMenu4.setText("Anicht");
        jMenu4.setName("view"); // NOI18N

        jMenuItem11.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F12, 0));
        jMenuItem11.setText("Fullscreen");
        jMenuItem11.setName("fullscreen"); // NOI18N
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem11);

        jMenuBar1.add(jMenu4);

        jMenu3.setText("Über");

        jMenuItem9.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        jMenuItem9.setText("Über JigSPuzzle");
        jMenuItem9.setName("about"); // NOI18N
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem9);

        jMenuItem10.setText("Auf Neue Version Prüfen");
        jMenuItem10.setName("check-new-version"); // NOI18N
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem10);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        JigSPuzzle.getInstance().exitProgram();
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        this.desktopPuzzleWindow.showPuzzleSettings();
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        this.desktopPuzzleWindow.showUiSettings();
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // show window for selecting a picture
        JFileChooser fileChooser = new JFileChooser();
        File selectedFile;

        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileView(new ThumbnailView(fileChooser, 50));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(SettingsController.getInstance().getLanguageText(1, 111), "bmp", "gif", "jpg", "jpeg", "png"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(SettingsController.getInstance().getLanguageText(1, 112), "bmp"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(SettingsController.getInstance().getLanguageText(1, 113), "gif"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(SettingsController.getInstance().getLanguageText(1, 114), "jpg", "jpeg"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(SettingsController.getInstance().getLanguageText(1, 115), "png"));
        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            // user canceled
            return;
        }
        selectedFile = fileChooser.getSelectedFile();

        // make a puzzle out of the file
        new Thread(() -> {
            try {
                PuzzleController.getInstance().newPuzzle(selectedFile);
            } catch (IOException ex) {
                new ErrorMessageDialog(SettingsController.getInstance().getLanguageText(1, 55),
                        SettingsController.getInstance().getLanguageText(1, 56),
                        ex.getMessage()).showDialog(this);
            }
        }).start();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        new Thread(() -> {
            PuzzleController.getInstance().shufflePuzzlepieces();
        }).start();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // show window for selecting
        JFileChooser fileChooser = new JFileChooser();
        File selectedFile;

        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FileNameExtensionFilter(SettingsController.getInstance().getLanguageText(1, 121), PuzzleController.PUZZLE_SAVES_ENDING));
        if (lastSavedFile == null) {
            if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                // user canceled
                return;
            }
            selectedFile = fileChooser.getSelectedFile();
            lastSavedFile = selectedFile;
        } else {
            selectedFile = lastSavedFile;
        }

        try {
            PuzzleController.getInstance().savePuzzle(selectedFile);
        } catch (IOException ex) {
            new ErrorMessageDialog(SettingsController.getInstance().getLanguageText(1, 51),
                    SettingsController.getInstance().getLanguageText(1, 52),
                    ex.getMessage()).showDialog(this);
        }
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // show window for selecting
        JFileChooser fileChooser = new JFileChooser();
        File selectedFile;

        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FileNameExtensionFilter(SettingsController.getInstance().getLanguageText(1, 121), PuzzleController.PUZZLE_SAVES_ENDING));
        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            // user canceled
            return;
        }
        selectedFile = fileChooser.getSelectedFile();

        try {
            PuzzleController.getInstance().loadPuzzle(selectedFile);
        } catch (IOException ex) {
            new ErrorMessageDialog(SettingsController.getInstance().getLanguageText(1, 53),
                    SettingsController.getInstance().getLanguageText(1, 54),
                    ex.getMessage()).showDialog(this);
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        new Thread(() -> {
            try {
                PuzzleController.getInstance().restartPuzzle();
            } catch (IOException ex) {
                new ErrorMessageDialog(SettingsController.getInstance().getLanguageText(1, 57),
                        SettingsController.getInstance().getLanguageText(1, 58),
                        ex.getMessage()).showDialog(this);
            }
        }).start();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        this.desktopPuzzleWindow.showVersionCheckWindow();
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        desktopPuzzleWindow.triggerFullscreen();
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        this.desktopPuzzleWindow.showAboutWindow();
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    // End of variables declaration//GEN-END:variables

}
