package dev.simplix.cirrus.velocity;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.simplix.cirrus.common.Cirrus;
import dev.simplix.cirrus.common.business.InventoryMenuItemWrapper;
import dev.simplix.cirrus.common.business.MenuItemWrapper;
import dev.simplix.cirrus.common.business.PlayerWrapper;
import dev.simplix.cirrus.common.converter.Converters;
import dev.simplix.cirrus.common.effect.MenuAnimator;
import dev.simplix.cirrus.common.item.CirrusItem;
import dev.simplix.cirrus.common.menu.MenuBuilder;
import dev.simplix.cirrus.velocity.converters.ItemModelConverter;
import dev.simplix.cirrus.velocity.converters.ItemStackConverter;
import dev.simplix.cirrus.velocity.converters.PlayerConverter;
import dev.simplix.cirrus.velocity.converters.PlayerUniqueIdConverter;
import dev.simplix.cirrus.velocity.listener.QuitListener;
import dev.simplix.cirrus.velocity.protocolize.ProtocolizeMenuBuilder;
import dev.simplix.protocolize.api.item.ItemStack;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Date: 18.09.2021
 *
 * @author Exceptionflug
 */
public class CirrusVelocity {

    private static ProxyServer proxyServer;

    public static void init(ProxyServer proxyServer, Object plugin) {
        if (CirrusVelocity.proxyServer != null) {
            return;
        }
        CirrusVelocity.proxyServer = proxyServer;
        Cirrus.registerService(MenuBuilder.class, new ProtocolizeMenuBuilder());
        proxyServer.getEventManager().register(plugin, new QuitListener());

        // Players
        Converters.register(Player.class, PlayerWrapper.class, new PlayerConverter());
        Converters.register(UUID.class, PlayerWrapper.class, new PlayerUniqueIdConverter(proxyServer));

        // Items
        Converters.register(ItemStack.class, MenuItemWrapper.class, new ItemStackConverter());
        Converters.register(CirrusItem.class, InventoryMenuItemWrapper.class, new ItemModelConverter());

        proxyServer.getScheduler().buildTask(plugin, () -> {
            if (proxyServer.getPlayerCount() > 0 && !MenuAnimator.isEmpty()) {
                MenuAnimator.updateAll();
            }
        }).repeat(50 * 2, TimeUnit.MILLISECONDS).schedule();
    }

}
