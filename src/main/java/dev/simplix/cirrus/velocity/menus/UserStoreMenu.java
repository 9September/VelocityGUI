package dev.simplix.cirrus.velocity.menus;

import com.velocitypowered.api.proxy.ProxyServer;
import dev.simplix.cirrus.common.business.PlayerWrapper;
import dev.simplix.cirrus.common.configuration.MenuConfiguration;
import dev.simplix.cirrus.common.menus.SimpleMenu;
import lombok.NonNull;

import java.util.Locale;

public class UserStoreMenu extends SimpleMenu {

    private final ProxyServer proxyServer;

    public UserStoreMenu(PlayerWrapper player, MenuConfiguration configuration, ProxyServer proxyServer) {
        super(player, configuration, Locale.ENGLISH);
        this.proxyServer = proxyServer;

        registerActionHandler("userstore", click -> {
            player().sendMessage("(임시)유저상점 테스트");
        });
    }
}
