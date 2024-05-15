package ncu.mac.pzdata.services;

import ncu.mac.pzdata.models.MemberModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface ExcelImportService {
    List<MemberModel> importFromExcel(InputStream inputStream, int SheetIndex) throws IOException;
}
