package sync.slamtalk.team.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import sync.slamtalk.mate.entity.RecruitedSkillLevelType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class FromTeamFormDTO {
    @NotBlank(message = "팀명을 입력해주세요.")
    private String teamName;

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NonNull
    private String content;

    @NotBlank(message = "상세 위치를 입력해주세요.")
    private String locationDetail;

    @NonNull
    private String numberOfMembers;

    @NonNull
    @Enumerated(EnumType.STRING)
    private RecruitedSkillLevelType skillLevel;

    @NonNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduledDate;

    @NonNull
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @NonNull
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;


    public String toString() {
        return "FromTeamFormDTO(teamName=" + this.getTeamName() + ", title=" + this.getTitle() +
                ", content=" + this.getContent() + ", locationDetail=" + this.getLocationDetail() +
                ", numberOfMembers=" + this.getNumberOfMembers() + ", skillLevel=" + this.getSkillLevel() +
                ", scheduledDate=" + this.getScheduledDate() + ", startTime=" + this.getStartTime() + ", endTime=" + this.getEndTime() + ")";
    }

}
