package ncu.mac.pzdata.services;

import ncu.mac.commons.helpers.ExcelKeyModelHelper;
import ncu.mac.pzdata.models.MemberModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class ExcelImportServiceImpl implements ExcelImportService {
    private static final Logger logger = LoggerFactory.getLogger(ExcelImportServiceImpl.class);


    @Override
    public List<MemberModel> importFromExcel(InputStream inputStream, int sheetIndex) throws IOException {
        final var helper = ExcelKeyModelHelper.getHelper(new MemberModel());
        return helper.importFromExcel(inputStream, sheetIndex);
    }
}
