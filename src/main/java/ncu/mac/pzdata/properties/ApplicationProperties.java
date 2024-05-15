package ncu.mac.pzdata.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("application")
public class ApplicationProperties {
    public static class ExcelDataModel {
        private String fileName;
        private int sheetIndex;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public int getSheetIndex() {
            return sheetIndex;
        }

        public void setSheetIndex(int sheetIndex) {
            this.sheetIndex = sheetIndex;
        }
    }

    public static class GraduationSettings {
        private int attendMinimum;
        private int makeupMaximum;
        private int absentMaximum;
        private int perfectAttendance;
        private int diligent;

        public int getAttendMinimum() {
            return attendMinimum;
        }

        public void setAttendMinimum(int attendMinimum) {
            this.attendMinimum = attendMinimum;
        }

        public int getMakeupMaximum() {
            return makeupMaximum;
        }

        public void setMakeupMaximum(int makeupMaximum) {
            this.makeupMaximum = makeupMaximum;
        }

        public int getAbsentMaximum() {
            return absentMaximum;
        }

        public void setAbsentMaximum(int absentMaximum) {
            this.absentMaximum = absentMaximum;
        }

        public int getPerfectAttendance() {
            return perfectAttendance;
        }

        public void setPerfectAttendance(int perfectAttendance) {
            this.perfectAttendance = perfectAttendance;
        }

        public int getDiligent() {
            return diligent;
        }

        public void setDiligent(int diligent) {
            this.diligent = diligent;
        }
    }

    private GraduationSettings graduationDefaults;
    private ExcelDataModel main;
    private ExcelDataModel attendRecord;
    private String outputFolder;

    public GraduationSettings getGraduationDefaults() {
        return graduationDefaults;
    }

    public void setGraduationDefaults(GraduationSettings graduationDefaults) {
        this.graduationDefaults = graduationDefaults;
    }

    public ExcelDataModel getMain() {
        return main;
    }

    public void setMain(ExcelDataModel main) {
        this.main = main;
    }

    public ExcelDataModel getAttendRecord() {
        return attendRecord;
    }

    public void setAttendRecord(ExcelDataModel attendRecord) {
        this.attendRecord = attendRecord;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }
}
