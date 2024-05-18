package ncu.mac.pzdata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import ncu.mac.commons.helpers.ExcelGridHelper;
import ncu.mac.commons.utils.ReadfileUtil;
import ncu.mac.commons.utils.StackTraceUtil;
import ncu.mac.pzdata.properties.ApplicationProperties;
import ncu.mac.pzdata.services.GraduateCalculationService;
import ncu.mac.pzdata.services.MemberService;
import ncu.mac.pzdata.services.UpgradeInvestigationService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
//@EnableReflectionForLogging
public class PzDataApplication implements CommandLineRunner {
    private final GraduateCalculationService graduateCalculationService;
    private final MemberService memberService;
    private final UpgradeInvestigationService upgradeInvestigationService;
    private final ApplicationProperties properties;

    public PzDataApplication(GraduateCalculationService graduateCalculationService, MemberService memberService, UpgradeInvestigationService upgradeInvestigationService, ApplicationProperties properties) {
        this.graduateCalculationService = graduateCalculationService;
        this.memberService = memberService;
        this.upgradeInvestigationService = upgradeInvestigationService;
        this.properties = properties;

//        org.apache.logging.log4j.message.DefaultFlowMessageFactory flowMessageFactory;
    }

    public static void main(String[] args) {
//        initialize();
        SpringApplication.run(PzDataApplication.class, args);
    }

//    public static void initialize() {
//        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
//        JoranConfigurator configurator = new JoranConfigurator();
//        configurator.setContext(context);
//        context.reset();
//        try {
//            // configurator.doConfigure("logback.xml");
//            configurator.doConfigure(new ByteArrayInputStream("""
//                    <?xml version="1.0" encoding="UTF-8"?>
//                    <configuration>
//                        <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
//                            <layout class="ch.qos.logback.classic.PatternLayout">
//                                <Pattern>
//                                    %d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n
//                                </Pattern>
//                            </layout>
//                        </appender>
//                        <logger name="ncu.mac" level="info" additivity="false">
//                            <appender-ref ref="CONSOLE"/>
//                        </logger>
//                        <root level="error">
//                            <appender-ref ref="CONSOLE"/>
//                        </root>
//                    </configuration>
//                    """.getBytes(StandardCharsets.US_ASCII)));
//        } catch (JoranException e) {
//            System.out.println(e.getMessage());
//        }
//    }

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


        writeDataGridToFile(directory, baseName + "-結業統計.xlsx", graduateCalculationService.fromExcel(graduationSettings,
                ReadfileUtil.readFrom(properties.getAttendRecord().getFileName()),
                properties.getAttendRecord().getSheetIndex()));

        writeDataGridToFile(directory, baseName + "-升班調查.xlsx",
                upgradeInvestigationService.generateInvestigationReport());


//        ReadfileUtil.readFrom(applicationProperties.getAttendRecord().getFileName())
//        graduateCalculationService.fromExcel()

//        StackTraceUtil.print1(memberModels);
    }

    private static void writeDataGridToFile(String directory, String outputFile, ExcelGridHelper.DataGrid dataGrid) throws IOException {
        final var os = new FileOutputStream(directory + outputFile);
        dataGrid.write(os);

        os.flush();
        os.close();

        System.out.println("write file. (" + outputFile + ")");
    }
}
