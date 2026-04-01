package ru.tbank.loganalyzer.render;

import ru.tbank.loganalyzer.model.AnalysisReport;

public interface ReportRenderer {
    String render(AnalysisReport report);
}
