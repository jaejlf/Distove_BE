package distove.community.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static distove.community.enumerate.DefaultRoleName.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roleName;
    private boolean canDeleteServer;
    private boolean canManageServer;
    private boolean canManageChannel;
    private boolean canUpdateMemberRole;

    @ManyToOne
    @JoinColumn(name = "server_id")
    private Server server;

    public static List<MemberRole> createDefaultRoles(Server server) {
        List<MemberRole> memberRoles = new ArrayList<>();
        memberRoles.add(MemberRole.builder().roleName(OWNER.getName())
                .canDeleteServer(true).canManageServer(true).canManageChannel(true).canUpdateMemberRole(true)
                .server(server).build());
        memberRoles.add(MemberRole.builder().roleName(MANAGER.getName())
                .canDeleteServer(false).canManageServer(true).canManageChannel(true).canUpdateMemberRole(true)
                .server(server).build());
        memberRoles.add(MemberRole.builder().roleName(MEMBER.getName())
                .canDeleteServer(false).canManageServer(false).canManageChannel(false).canUpdateMemberRole(false)
                .server(server).build());
        return memberRoles;
    }

}