package ncu.mac.pzdata.services;

import ncu.mac.commons.helpers.ExcelGridHelper;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class UpgradeInvestigationServiceImpl implements UpgradeInvestigationService {
    private final MemberService memberService;

    public UpgradeInvestigationServiceImpl(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public ExcelGridHelper.DataGrid generateInvestigationReport() {
        final var dataGrid = ExcelGridHelper.getInstance("Sheet1");
        final var rowNumber = new AtomicInteger(1);
        final var index = new AtomicInteger(0);
        dataGrid.putValue(0, 0, "序");
        dataGrid.putValue(0, 1, "班級");
        dataGrid.putValue(0, 2, "學長");
        dataGrid.putValue(0, 3, "組別");
        dataGrid.putValue(0, 4, "姓名");
        dataGrid.putValue(0, 5, "姓別");
        dataGrid.putValue(0, 6, "舊制最高禪修班別");
        dataGrid.putValue(0, 7, "新制最高禪修班別");
        dataGrid.putValue(0, 8, "");
        dataGrid.putValue(0, 9, "");
        dataGrid.putValue(0, 10, "原因");
        dataGrid.putValue(0, 11, "結業");
        dataGrid.putValue(0, 12, "請假");
        dataGrid.putValue(0, 13, "補課");

        memberService.allMembers().stream()
                .filter(memberModel -> memberModel.getGraduationInfoModel() != null)
                .forEach(memberModel -> {
                    final var graduationInfoModel = memberModel.getGraduationInfoModel();

                    dataGrid.putValue(rowNumber.get(), 0, index.incrementAndGet());
                    dataGrid.putValue(rowNumber.get(), 1, memberModel.getClassName());
                    dataGrid.putValue(rowNumber.get(), 2, memberModel.getSeniorName());
                    dataGrid.putValue(rowNumber.get(), 3, memberModel.getClassGroup());
                    dataGrid.putValue(rowNumber.get(), 4, memberModel.getStudentName());
                    dataGrid.putValue(rowNumber.get(), 5, memberModel.getGender());
                    dataGrid.putValue(rowNumber.get(), 11, graduationInfoModel.graduation() ? "V": "");
                    dataGrid.putValue(rowNumber.get(), 12, graduationInfoModel.personalLeave());
                    dataGrid.putValue(rowNumber.get(), 13, graduationInfoModel.makeup());

                    rowNumber.incrementAndGet();
                });
        return dataGrid;
    }
}
