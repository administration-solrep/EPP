package fr.sword.idl.naiad.nuxeo.feuilleroute.api.service;

import java.util.Map;

public interface StepProcessInit extends StepProcess {
    void init(Map<String, String> params);
}
