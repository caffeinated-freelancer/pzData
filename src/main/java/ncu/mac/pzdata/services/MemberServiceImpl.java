package ncu.mac.pzdata.services;

import ncu.mac.commons.utils.ReadfileUtil;
import ncu.mac.pz.helpers.PuzhongGeneralHelper;
import ncu.mac.pzdata.models.MemberModel;
import ncu.mac.pzdata.properties.ApplicationProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class MemberServiceImpl implements MemberService {
    private final ExcelImportService excelImportService;
    private final ApplicationProperties properties;
    private final Map<String, MemberModel> membersByStudentId = new HashMap<>();

    public MemberServiceImpl(ExcelImportService excelImportService, ApplicationProperties properties) {
        this.excelImportService = excelImportService;
        this.properties = properties;
    }

    @Override
    public void loadMembers() throws IOException {
        final List<MemberModel> memberModels = excelImportService.importFromExcel(
                ReadfileUtil.readFrom(properties.getMain().getFileName()),
                properties.getMain().getSheetIndex());

        memberModels.forEach(memberModel -> {
            if (PuzhongGeneralHelper.validStudentId(memberModel.getStudentId())) {
                membersByStudentId.put(memberModel.getStudentId(), memberModel);
            } else {
                System.out.printf("Student ID: %s invalid\n", memberModel.getStudentId());
            }
        });
    }

    @Override
    public Optional<MemberModel> getMemberByStudentId(String studentId) {
        return Optional.ofNullable(membersByStudentId.get(studentId));
    }

    @Override
    public List<MemberModel> allMembers() {
        return new ArrayList<>(membersByStudentId.values());
    }
}
