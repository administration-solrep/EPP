package fr.dila.ss.api.pdf;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public interface Cell {
    void display(XWPFParagraph paragraph, XWPFRun run);
}
