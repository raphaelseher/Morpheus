package at.rags.morpheus.resources;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import at.rags.morpheus.Resource;
import at.rags.morpheus.annotations.JsonApiType;
import at.rags.morpheus.annotations.Relationship;

@JsonApiType("ChatSession")
public class ChatSession extends Resource implements Serializable {

    public static final String STATE_INITIATED = "initiated";
    public static final String STATE_STARTED = "started";
    public static final String STATE_ENDED = "ended";

    @Relationship("patient")
    @SerializedName("patient")
    private BasicPerson patient;
    @Relationship("expert")
    @SerializedName("expert")
    private BasicExpert expert;
    @Relationship("requested_expert")
    @SerializedName("requested_expert")
    private BasicExpert requestedExpert;
    @Relationship("chat_room")
    @SerializedName("chat_room")
    private ChatRoom chatRoom;

    @SerializedName("start_time")
    private int startTime;
    @SerializedName("end_time")
    private int endTime;
    @SerializedName("state")
    private String state;
    @SerializedName("consult_type")
    private String consultType;
    @SerializedName("reason_for_visit")
    private String reasonForVisit;
    @SerializedName("consult_geo_state")
    private String consultGeoState;
    @SerializedName("consult_geo_country")
    private String consultGeoCountry;
    @SerializedName("concierge_appointment_id")
    private String appointmentId;

    public String getState() {
        return state;
    }

    public String getConsultType() {
        return consultType;
    }

    public String getReasonForVisit() {
        return reasonForVisit;
    }

    public String getConsultGeoState() {
        return consultGeoState;
    }

    public String getConsultGeoCountry() {
        return consultGeoCountry;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public BasicPerson getPatient() {
        return patient;
    }

    public BasicExpert getExpert() {
        return expert;
    }

    public BasicExpert getRequestedExpert() {
        return requestedExpert;
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

}
