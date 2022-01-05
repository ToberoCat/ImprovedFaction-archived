package extension.chatmessageextension.messages;

import java.util.ArrayList;
import java.util.List;

public class History {

	private List<ChatMessage> messages = new ArrayList<>();
	
	public History() {
		
	}

	public List<ChatMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<ChatMessage> messages) {
		this.messages = messages;
	}
	
	
	
}
