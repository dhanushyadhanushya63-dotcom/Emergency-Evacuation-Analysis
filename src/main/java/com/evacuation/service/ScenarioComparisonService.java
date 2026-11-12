package com.evacuation.service;

import com.evacuation.dto.RiskAnalysisDto;
import com.evacuation.dto.ScenarioComparisonDto;
import com.evacuation.entity.Scenario;
import com.evacuation.repository.ScenarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ScenarioComparisonService {

    private final ScenarioRepository scenarioRepository;
    private final RiskAnalysisService riskAnalysisService;

    public ScenarioComparisonService(ScenarioRepository scenarioRepository,
                                     RiskAnalysisService riskAnalysisService) {
        this.scenarioRepository = scenarioRepository;
        this.riskAnalysisService = riskAnalysisService;
    }

    public ScenarioComparisonDto compareScenarios(List<Long> scenarioIds) {
        List<Scenario> targetScenarios;
        if (scenarioIds != null && !scenarioIds.isEmpty()) {
            targetScenarios = scenarioRepository.findAllById(scenarioIds);
        } else {
            targetScenarios = scenarioRepository.findAll();
        }

        List<ScenarioComparisonDto.ScenarioComparisonItem> items = new ArrayList<>();

        for (Scenario sc : targetScenarios) {
            RiskAnalysisDto risk = riskAnalysisService.analyzeScenarioRisk(sc.getId());

            ScenarioComparisonDto.ScenarioComparisonItem item = new ScenarioComparisonDto.ScenarioComparisonItem();
            item.setScenarioId(sc.getId());
            item.setScenarioName(sc.getName());
            item.setScenarioType(sc.getScenarioType());
            item.setTotalEvacuationTime(risk.getMaxEvacuationTimeSeconds());
            item.setAverageEvacuationTime(risk.getAvgEvacuationTimeSeconds());
            item.setTotalOccupants(risk.getTotalOccupants());
            item.setSafelyEvacuated(risk.getTotalOccupants() - risk.getTrappedOccupants());
            item.setTrappedOccupants(risk.getTrappedOccupants());
            item.setAvailableExits(risk.getAvailableExitsCount());
            item.setBlockedCorridors(risk.getBlockedCorridorsCount());
            item.setPeakCongestionRatio(risk.getOverallCongestionIndex());
            item.setOverallRiskLevel(risk.getOverallRiskLevel());

            long highCount = risk.getCorridorRisks().stream()
                    .filter(c -> "HIGH".equalsIgnoreCase(c.getRiskCategory()) || c.getRiskCategory().contains("BLOCKED"))
                    .count();
            item.setHighRiskCorridorsCount((int) highCount);

            items.add(item);
        }

        ScenarioComparisonDto dto = new ScenarioComparisonDto();
        dto.setScenarios(items);

        if (!items.isEmpty()) {
            ScenarioComparisonDto.ScenarioComparisonItem best = items.stream()
                    .filter(i -> i.getTrappedOccupants() == 0)
                    .min(Comparator.comparingDouble(ScenarioComparisonDto.ScenarioComparisonItem::getTotalEvacuationTime))
                    .orElse(items.get(0));

            ScenarioComparisonDto.ScenarioComparisonItem worst = items.stream()
                    .max(Comparator.comparingInt(ScenarioComparisonDto.ScenarioComparisonItem::getTrappedOccupants)
                            .thenComparingDouble(ScenarioComparisonDto.ScenarioComparisonItem::getTotalEvacuationTime))
                    .orElse(items.get(0));

            dto.setMostOptimalScenario(best.getScenarioName() + " (" + best.getTotalEvacuationTime() + "s)");
            dto.setHighestRiskScenario(worst.getScenarioName() + " (" + worst.getTotalEvacuationTime() + "s, " + worst.getTrappedOccupants() + " trapped)");

            double delta = worst.getTotalEvacuationTime() - best.getTotalEvacuationTime();
            dto.setComparisonSummary("Cross-scenario differential analysis shows a clearance time gap of "
                    + Math.round(delta * 10.0) / 10.0 + " seconds between optimal (" + best.getScenarioName()
                    + ") and highest risk (" + worst.getScenarioName() + ").");
        }

        return dto;
    }
}
