package ncu.mac.pzdata.services;

import ncu.mac.pzdata.models.MemberModel;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface MemberService {
    void loadMembers() throws IOException;

    Optional<MemberModel> getMemberByStudentId(String studentId);

    List<MemberModel> allMembers();
}
