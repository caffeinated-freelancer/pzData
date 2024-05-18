package ncu.mac.pzdata.services;

import ncu.mac.commons.helpers.ExcelGridHelper;

public interface UpgradeInvestigationService {
    ExcelGridHelper.DataGrid generateInvestigationReport();
}
