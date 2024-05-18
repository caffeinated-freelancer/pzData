package ncu.mac.pzdata.models;

import ncu.mac.commons.annotations.KeyName;
import ncu.mac.commons.models.HaveKeyValues;

public class MemberModel extends HaveKeyValues.HaveKeyValuesImpl implements HaveKeyValues {
    @KeyName("學員編號")
    private String studentId;
    @KeyName("姓名")
    private String studentName;
    @KeyName("學長")
    private String seniorName;
    @KeyName("班級")
    private String className;
    @KeyName("執事")
    private String deacon;
    @KeyName("法名")
    private String dharmaName;
    @KeyName("組別")
    private String classGroup;
    @KeyName("性別")
    private String gender;
    @KeyName("手機")
    private String mobilePhoneNumber;
    @KeyName("住宅")
    private String homePhoneNumber;
    @KeyName("介紹人&備註")
    private String memo;
    private GraduationInfoModel graduationInfoModel;


//    private final SequencedMap<String, String> sequencedMap = new LinkedHashMap<>();
//
//    @Override
//    public SequencedSet<String> getKeySet() {
//        return sequencedMap.sequencedKeySet();
//    }
//
//    @Override
//    public Optional<String> getValue(String key) {
//        if (sequencedMap.containsKey(key)) {
//            return Optional.of(sequencedMap.get(key));
//        }
//        return Optional.empty();
//    }
//
//    @Override
//    public void putValue(String key, String value) {
//        if (!sequencedMap.containsKey(key)) {
//            sequencedMap.put(key, value);
//        }
//    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getSeniorName() {
        return seniorName;
    }

    public void setSeniorName(String seniorName) {
        this.seniorName = seniorName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDeacon() {
        return deacon;
    }

    public void setDeacon(String deacon) {
        this.deacon = deacon;
    }

    public String getDharmaName() {
        return dharmaName;
    }

    public void setDharmaName(String dharmaName) {
        this.dharmaName = dharmaName;
    }

    public String getClassGroup() {
        return classGroup;
    }

    public void setClassGroup(String classGroup) {
        this.classGroup = classGroup;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public String getHomePhoneNumber() {
        return homePhoneNumber;
    }

    public void setHomePhoneNumber(String homePhoneNumber) {
        this.homePhoneNumber = homePhoneNumber;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public GraduationInfoModel getGraduationInfoModel() {
        return graduationInfoModel;
    }

    public void setGraduationInfoModel(GraduationInfoModel graduationInfoModel) {
        this.graduationInfoModel = graduationInfoModel;
    }
}
