package extension.chatmessageextension.messages;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class ChatMessage {

	public static final String datePattern = "dd/MM/yyyy HH:mm z";
	
	private String message;
	private UUID playerUUID;
	private Date date;
	
	public ChatMessage() {
		
	}

	public ChatMessage(String message, UUID playerUUID) {
		super();
		this.message = message;
		this.playerUUID = playerUUID;
		this.date = Calendar.getInstance().getTime();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public UUID getPlayerUUID() {
		return playerUUID;
	}

	public void setPlayerUUID(UUID playerUUID) {
		this.playerUUID = playerUUID;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
