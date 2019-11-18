/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package funcoes;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.JViewport;

/**
 *
 * @author User
 */
public class ScroollJTable {

    public static void scroollAutoEnd(JTable jTable1) {
        int linha = jTable1.getRowCount() - 1;
        jTable1.setRowSelectionInterval(linha, linha);
        setViewPortPosition((JViewport) jTable1.getParent(), jTable1.getCellRect(linha, 0, true));
    }

    public static void scrollToVisible(JTable tb, int indexLinha) {
        scrollToVisible(tb, indexLinha, 0);
    }

    public static void scrollToVisible(JTable tb, int indexLinha, int indexColuna) {
        if (!(tb.getParent() instanceof JViewport)) {
            return;
        }

        setViewPortPosition((JViewport) tb.getParent(), tb.getCellRect(
                indexLinha, indexColuna, true));
    }

    public static Collection<Integer> getReverseSelectedRows(JTable table) {
        Set<Integer> rows = new TreeSet<Integer>(new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                return o2.compareTo(o1);
            }
        });

        for (int r : table.getSelectedRows()) {
            rows.add(r);
        }

        return rows;
    }

    public static void selectAndScroll(JTable tb, int indexLinha) {
        tb.getSelectionModel().setSelectionInterval(indexLinha, indexLinha);
        scrollToVisible(tb, indexLinha);
    }

    public static void scrollToSelection(JTree arvore) {
        if (!(arvore.getParent() instanceof JViewport)) {
            return;
        }

        setViewPortPosition((JViewport) arvore.getParent(),
                arvore.getPathBounds(arvore.getSelectionPath()));

    }

    public static void setViewPortPosition(JViewport viewport, Rectangle posicao) {
        // The location of the viewport relative to the object
        Point ponto = viewport.getViewPosition();

        // Translate the cell location so that it is relative
        // to the view, assuming the northwest corner of the
        // view is (0,0)
        posicao.setLocation(posicao.x - ponto.x, posicao.y - ponto.y);

        // Scroll the area into view
        viewport.scrollRectToVisible(posicao);

        /*
        example:
                linha x = 10
                jTable1.setRowSelectionInterval(x, x);
                setPosicaoViewPort((JViewport) jTable1.getParent(), jTable1.getCellRect(x, 0, true));
         */
    }

    public static void expandAllNodes(JTree arvore) {
        for (int i = 0; i < arvore.getRowCount(); i++) {
            arvore.expandRow(i);
        }
    }

    public static void expandFirstNodes(JTree arvore) {
        for (int i = arvore.getRowCount() - 1; i >= 0; i--) {
            arvore.expandRow(i);
        }
    }

    /**
     * Encode a color in a string in RGB format. This string is compatible to
     * HTML format.
     *
     * @param color The color to encode (e.g. Color.RED)
     * @return The encoded color (e.g. FF0000)
     */
    public static String encodeColor(Color color) {
        if (color == null) {
            return "000000";
        }

        return String.format("%02x%02x%02x", color.getRed(), color.getGreen(),
                color.getBlue());
    }

    public static Component getOwnerWindow(Component component) {
        Component parent = component;
        while (parent != null && !(parent instanceof Frame)
                && !(parent instanceof Dialog)) {
            parent = parent.getParent();
        }
        return parent;
    }
}
