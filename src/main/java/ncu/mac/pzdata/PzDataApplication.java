package ncu.mac.pzdata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import ncu.mac.commons.utils.ReadfileUtil;
import ncu.mac.commons.utils.StackTraceUtil;
import ncu.mac.pzdata.properties.ApplicationProperties;
import ncu.mac.pzdata.services.GraduateCalculationService;
import ncu.mac.pzdata.services.MemberService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
public class PzDataApplication implements CommandLineRunner {
    private final GraduateCalculationService graduateCalculationService;
    private final MemberService memberService;
    private final ApplicationProperties properties;

    public PzDataApplication(GraduateCalculationService graduateCalculationService, MemberService memberService, ApplicationProperties properties) {
        this.graduateCalculationService = graduateCalculationService;
        this.memberService = memberService;
        this.properties = properties;
    }

    public static void main(String[] args) {
        SpringApplication.run(PzDataApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        memberService.loadMembers();

        final var directory = FilenameUtils.getFullPath(properties.getAttendRecord().getFileName());
        final var baseName = FilenameUtils.getBaseName(properties.getAttendRecord().getFileName());
        final var settingFile = directory + baseName + ".yml";

        final ApplicationProperties.GraduationSettings graduationSettings;

        if (Files.exists(Paths.get(settingFile))) {
            System.out.println(settingFile);
            final var mapper = new ObjectMapper(new YAMLFactory());
            mapper.findAndRegisterModules();
            final var settings = mapper.readValue(new File(settingFile), ApplicationProperties.GraduationSettings.class);

            StackTraceUtil.print1(settings);
            graduationSettings = settings;
        } else {
            StackTraceUtil.print1(properties.getGraduationDefaults());
            graduationSettings = properties.getGraduationDefaults();
        }

        final var dataGrid = graduateCalculationService.fromExcel(graduationSettings,
                ReadfileUtil.readFrom(properties.getAttendRecord().getFileName()),
                properties.getAttendRecord().getSheetIndex());

        final var os = new FileOutputStream(directory + baseName + "-output.xlsx");
        dataGrid.write(os);

        os.flush();
        os.close();

        System.out.println("write file. (" + baseName + "-output.xlsx)");


//        ReadfileUtil.readFrom(applicationProperties.getAttendRecord().getFileName())
//        graduateCalculationService.fromExcel()

//        StackTraceUtil.print1(memberModels);
    }
}
