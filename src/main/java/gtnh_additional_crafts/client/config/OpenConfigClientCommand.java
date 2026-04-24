package gtnh_additional_crafts.client.config;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class OpenConfigClientCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "tbui";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/tbui";
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("tbconfig", "thaumicbootsconfig", "bootsconfig");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] arguments) {
        InGameConfigOpener.open();
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
