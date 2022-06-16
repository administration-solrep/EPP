package fr.dila.ss.ui.bean.fdr;

import java.util.ArrayList;
import java.util.List;

public class FdrTableDTO {
    private List<EtapeDTO> lines = new ArrayList<>();
    private Integer totalNbLevel = 0;

    public FdrTableDTO() {
        super();
    }

    public List<EtapeDTO> getLines() {
        return lines;
    }

    public void setLines(List<EtapeDTO> lines) {
        this.lines = lines;
    }

    public Integer getTotalNbLevel() {
        return totalNbLevel;
    }

    public void setTotalNbLevel(Integer totalNbLevel) {
        this.totalNbLevel = totalNbLevel;
    }
}
