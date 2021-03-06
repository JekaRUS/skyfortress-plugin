package ru.jekarus.skyfortress.v3.listener;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import ru.jekarus.skyfortress.v3.SkyFortressPlugin;
import ru.jekarus.skyfortress.v3.game.SfGameStageType;
import ru.jekarus.skyfortress.v3.player.PlayerData;
import ru.jekarus.skyfortress.v3.player.PlayersDataContainer;
import ru.jekarus.skyfortress.v3.team.SfGameTeam;
import ru.jekarus.skyfortress.v3.team.SfTeam;
import ru.jekarus.skyfortress.v3.utils.LocationAndRotation;

public class PlayerRespawnListener {

    private final SkyFortressPlugin plugin;
    private PlayersDataContainer playersData;

    public PlayerRespawnListener(SkyFortressPlugin plugin) {
        this.plugin = plugin;
        this.playersData = plugin.getPlayersDataContainer();
    }

    public void register() {
        Sponge.getEventManager().registerListeners(this.plugin, this);
    }

    public void unregister() {
        Sponge.getEventManager().unregisterListeners(this);
    }

    @Listener
    public void onRespawn(RespawnPlayerEvent event, @Getter("getTargetEntity") Player player) {
        PlayerData playerData = this.playersData.getOrCreateData(player);
        SfTeam playerTeam = playerData.getTeam();
        SfGameStageType gameStage = plugin.getGame().getStage();
        if (playerTeam.getType() == SfTeam.Type.GAME && gameStage == SfGameStageType.IN_GAME) {
            player.sendMessage(
                    ChatTypes.ACTION_BAR, Text.EMPTY
            );
            SfGameTeam gameTeam = (SfGameTeam) playerTeam;
            LocationAndRotation respawn = gameTeam.getCastle().getRandomRespawn();
            event.setToTransform(new Transform<>(
                    respawn.getLocation().getExtent(),
                    respawn.getLocation().getPosition(),
                    respawn.getRotation()
            ));
            player.getOrCreate(PotionEffectData.class).ifPresent(effects -> {
                effects.addElement(PotionEffect.builder().potionType(PotionEffectTypes.STRENGTH).duration(60).amplifier(0).particles(false).build());
                effects.addElement(PotionEffect.builder().potionType(PotionEffectTypes.RESISTANCE).duration(60).amplifier(0).particles(false).build());
            });
        }

    }

}
