package dev.simplix.cirrus.velocity.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.simplix.cirrus.common.Cirrus;
import dev.simplix.cirrus.common.business.PlayerWrapper;
import dev.simplix.cirrus.common.converter.Converters;
import dev.simplix.cirrus.velocity.menus.WarpMenu;

public class TestCommand implements SimpleCommand {

    ProxyServer proxyServer;

    public TestCommand(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Override
    public void execute(Invocation invocation) {
        if (invocation.source() instanceof Player) {
            Player player = (Player) invocation.source();
            new WarpMenu(Converters.convert(player, PlayerWrapper.class),
                    Cirrus.configurationFactory().loadFile("plugins/VelocityGUI/menu.json"), proxyServer).open();
        }
    }
}
