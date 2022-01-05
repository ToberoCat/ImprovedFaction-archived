package extension.chatmessageextension.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommandSettings;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommandSettings.NYI;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;

public class ChatSubCommand extends SubCommand {

	private List<SubCommand> subCommands = new ArrayList<>();
	
	public ChatSubCommand() {
		super("chat", "Send a message to all faction members");
		subCommands.add(new Mute());
		subCommands.add(new SendMessage());
	}

	@Override
	public SubCommandSettings getSettings() {
		return super.getSettings()
				.setAllowAliases(true)
				.setNeedsAdmin(false)
				.setNeedsFaction(NYI.Yes);
	}
	
	@Override
	protected void CommandExecute(Player arg0, String[] arg1) {
		if (!CallSubCommands(subCommands, arg0, arg1)) {
			Language.sendMessage(LangMessage.THIS_COMMAND_DOES_NOT_EXIST, arg0);
		}
	}

	@Override
	protected List<String> CommandTab(Player arg0, String[] arg1) {
        return SubCommand.CallSubCommandsTab(subCommands, arg0, arg1);
	}

}
