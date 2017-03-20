package at.rags.morpheus.Resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import at.rags.morpheus.Resource;
import at.rags.morpheus.annotations.JsonApiType;
import at.rags.morpheus.annotations.Relationship;

@JsonApiType("ClinicalQueueItem")
public class ClinicalQueueItem extends Resource {

    @SerializedName("invite_type")
    private String inviteType;
    @SerializedName("clinical_status")
    private String clinicalStatus;
    @SerializedName("routed_at")
    private String routedAt;
    @SerializedName("created_at")
    private String createdAt;

    @Relationship("chat_session")
    private ChatSession chatSession;
    @Relationship("inviter")
    private BasicPerson patient;
    @Relationship("invitee")
    private BasicExpert expert;

    public String getInviteType() {
        return inviteType;
    }

    public String getClinicalStatus() {
        return clinicalStatus;
    }

    public String getRoutedAt() {
        return routedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public ChatSession getChatSession() {
        return chatSession;
    }

    public BasicPerson getPatient() {
        return patient;
    }

    public BasicExpert getExpert() {
        return expert;
    }

}
