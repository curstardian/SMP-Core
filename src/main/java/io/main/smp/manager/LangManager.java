package io.main.smp.manager;

import io.main.smp.Msg;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumMap;

public class LangManager {

    private static final EnumMap<Msg, String> EN = new EnumMap<>(Msg.class);
    private static final EnumMap<Msg, String> KR = new EnumMap<>(Msg.class);

    static {
        p(Msg.PLAYER_ONLY,           "Only players can use this command.",                          "플레이어만 사용 가능합니다.");

        p(Msg.COMBAT_ENTER,          "[Combat] Entered combat! (30 min)",                           "[전투] 전투 상태 돌입! (30분)");
        p(Msg.COMBAT_CHAT_INFO,      "Commands require 15s without combat damage.",                 "명령어는 전투 피해 없이 15초 후 사용 가능합니다.");
        p(Msg.COMBAT_EXPIRE,         "[Combat] Combat ended.",                                      "[전투] 전투 종료.");
        p(Msg.COMBAT_DEATH_OTHER,    "[Combat] Opponent died, combat ended.",                       "[전투] 상대방 사망으로 전투 종료.");
        p(Msg.COMBAT_QUIT_OTHER,     "[Combat] Opponent disconnected, combat ended.",               "[전투] 상대방 접속 종료로 전투 종료.");
        p(Msg.COMBAT_NOT_IN,         "You are not in combat.",                                      "전투 상태가 아닙니다.");

        p(Msg.CANCEL_DONE,           "[Combat] Combat cancelled.",                                  "[전투] 전투가 취소되었습니다.");
        p(Msg.CANCEL_WAITING,        "[Combat] Waiting for opponent to accept...",                  "[전투] 상대방의 수락을 기다리는 중...");
        p(Msg.CANCEL_REQUEST_HEADER, "[Combat] {name} requested to cancel combat.",                 "[전투] {name}님이 전투 취소를 요청했습니다.");
        p(Msg.CANCEL_CONFIRM_QUESTION, "Cancel combat?",                                            "전투를 취소 하시겠습니까?");
        p(Msg.CANCEL_YES_BTN,        "[Yes]",                                                       "[예]");
        p(Msg.CANCEL_NO_BTN,         "[No]",                                                        "[아니오]");
        p(Msg.CANCEL_NO_REQUEST,     "No cancel request received.",                                 "받은 전투 취소 요청이 없습니다.");
        p(Msg.CANCEL_DENIED_SELF,    "[Combat] Cancel request denied.",                             "[전투] 전투 취소 요청을 거절했습니다.");
        p(Msg.CANCEL_DENIED_OTHER,   "[Combat] {name} denied your cancel request.",                 "[전투] {name}님이 전투 취소를 거절했습니다.");

        p(Msg.CMD_BLOCKED,           "Command blocked — {seconds}s without combat hit needed.",     "명령어 사용 불가 — 전투 피해 없이 {seconds}초 필요합니다.");

        p(Msg.ACTION_BAR_COMBAT,     "⚔ In Combat",                                                "⚔ 전투 중");
        p(Msg.ACTION_BAR_CMD_LOCK,   "⚔ In Combat | Cmd: {seconds}s",                              "⚔ 전투 중 | 명령어: {seconds}초");
        p(Msg.BOSSBAR_TITLE,         "⚔ In Combat | Leaving = Death",                              "⚔ 전투 중 | 나가면 죽습니다");

        p(Msg.TPA_USAGE,             "Usage: /tpa <player>",                                        "사용법: /tpa <플레이어>");
        p(Msg.TPA_NOT_FOUND,         "Player not found.",                                           "해당 플레이어를 찾을 수 없습니다.");
        p(Msg.TPA_SELF,              "You cannot request to yourself.",                             "자신에게 요청할 수 없습니다.");
        p(Msg.TPA_SENT,              "Teleport request sent to {name}.",                            "{name}님에게 텔레포트 요청을 보냈습니다.");
        p(Msg.TPA_RECEIVED,          "{name} wants to teleport to you. /tpaccept | /tpdeny",        "{name}님이 텔레포트를 요청했습니다. /tpaccept 수락 | /tpdeny 거절");
        p(Msg.TPA_EXPIRED_SENDER,    "Teleport request expired.",                                   "텔레포트 요청이 만료되었습니다.");
        p(Msg.TPA_EXPIRED_TARGET,    "{name}'s request expired.",                                   "{name}님의 요청이 만료되었습니다.");
        p(Msg.TPA_NO_REQUEST,        "No pending teleport request.",                                "대기 중인 텔레포트 요청이 없습니다.");
        p(Msg.TPA_OFFLINE,           "Requester is offline.",                                       "요청자가 오프라인 상태입니다.");
        p(Msg.TPA_ACCEPT_TARGET,     "Accepted {name}'s request.",                                  "{name}님의 요청을 수락했습니다.");
        p(Msg.TPA_ACCEPT_REQUESTER,  "Teleported to {name}.",                                       "{name}님에게 텔레포트되었습니다.");
        p(Msg.TPA_ACCEPT_TARGET2,    "{name} teleported to you.",                                   "{name}님이 텔레포트되었습니다.");
        p(Msg.TPA_DENY_SELF,         "Teleport request denied.",                                    "텔레포트 요청을 거절했습니다.");
        p(Msg.TPA_DENY_OTHER,        "{name} denied your teleport request.",                        "{name}님이 텔레포트 요청을 거절했습니다.");

        p(Msg.HOME_USAGE_SET,        "Usage: /sethome <name>",                                      "사용법: /sethome <이름>");
        p(Msg.HOME_INVALID_NAME,     "Home name can only contain letters, numbers, underscores, and Korean characters.", "집 이름에는 영문, 숫자, 밑줄, 한글만 사용할 수 있습니다.");
        p(Msg.HOME_SET,              "Home '{name}' set.",                                           "'{name}' 집이 설정되었습니다.");
        p(Msg.HOME_MAX,              "You can have at most 3 homes.",                               "집은 최대 3개까지 설정할 수 있습니다.");
        p(Msg.HOME_USAGE_DEL,        "Usage: /delhome <name>",                                      "사용법: /delhome <이름>");
        p(Msg.HOME_DEL,              "Home '{name}' deleted.",                                      "'{name}' 집이 삭제되었습니다.");
        p(Msg.HOME_NOT_FOUND,        "Home '{name}' not found.",                                    "'{name}' 집을 찾을 수 없습니다.");
        p(Msg.HOME_NONE,             "No homes set. Use /sethome <name>.",                          "설정된 집이 없습니다. /sethome <이름> 으로 집을 설정하세요.");
        p(Msg.HOME_LIST,             "Homes: {list}",                                               "집 목록: {list}");
        p(Msg.HOME_TELEPORT,         "Teleported to home '{name}'.",                                "'{name}' 집으로 이동했습니다.");

        p(Msg.BACK_NO_LOCATION,      "No location to return to, or it has expired (1 minute limit).", "복귀할 위치가 없거나 만료되었습니다. (1분 이내에만 가능)");
        p(Msg.BACK_SUCCESS,          "Teleported to your last death location.",                     "마지막으로 죽은 자리로 이동했습니다.");

        p(Msg.LOGOUT_KICK,           "Goodbye!",                                                    "안녕히 가세요!");

        p(Msg.CHAT_COMBAT,           "[Combat] Cannot chat while in combat.",                       "[전투] 전투 중에는 채팅을 사용할 수 없습니다.");
        p(Msg.CHAT_COOLDOWN,         "Sending messages too fast. Try again in {seconds} seconds.",  "메세지를 너무 빨리 보내고 있습니다. {seconds}초 후에 다시 시도하세요.");
        p(Msg.CHAT_DUPLICATE,        "Cannot send the same message consecutively.",                 "같은 메세지를 연속으로 보낼 수 없습니다.");

        p(Msg.LANG_USAGE,            "Usage: /smplang <kr|en>",                                     "사용법: /smplang <kr|en>");
        p(Msg.LANG_SET,              "Language changed to {lang}.",                                  "언어가 {lang}으로 변경되었습니다.");
        p(Msg.LANG_INVALID,          "Invalid language. Use 'kr' or 'en'.",                         "유효하지 않은 언어입니다. 'kr' 또는 'en'을 입력하세요.");
        p(Msg.LANG_NO_PERMISSION,    "You don't have permission to use this command.",               "이 명령어를 사용할 권한이 없습니다.");
    }

    private static void p(Msg key, String en, String kr) {
        EN.put(key, en);
        KR.put(key, kr);
    }

    private final JavaPlugin plugin;
    private boolean korean;

    public LangManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.korean = "kr".equalsIgnoreCase(plugin.getConfig().getString("language", "en"));
    }

    public String get(Msg key) {
        return (korean ? KR : EN).getOrDefault(key, key.name());
    }

    // pairs: "placeholder", "value", "placeholder2", "value2", ...
    public String get(Msg key, String... pairs) {
        String msg = get(key);
        for (int i = 0; i + 1 < pairs.length; i += 2) {
            msg = msg.replace("{" + pairs[i] + "}", pairs[i + 1]);
        }
        return msg;
    }

    public void setLanguage(String lang) {
        this.korean = "kr".equalsIgnoreCase(lang);
        plugin.getConfig().set("language", lang.toLowerCase());
        plugin.saveConfig();
    }

    public String currentLang() {
        return korean ? "kr" : "en";
    }
}
