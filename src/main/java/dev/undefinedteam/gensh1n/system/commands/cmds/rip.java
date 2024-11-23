package dev.undefinedteam.gensh1n.system.commands.cmds;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.undefinedteam.gensh1n.system.commands.Command;
import dev.undefinedteam.gensh1n.utils.json.GsonUtils;
import dev.undefinedteam.gensh1n.utils.network.Http;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.command.CommandSource;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

@StringEncryption
@ControlFlowObfuscation
public class rip extends Command {
    public rip() {
        super("kaihu", "removed", "kh");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("lol").then(argument("number", StringArgumentType.string()).executes(context -> {
            Runnable run = () -> {
            String lol;
            var response = "";
            LOLInfo info;

            lol = StringArgumentType.getString(context,"number");
                try {
                    response = Http.get("https://zy.xywlapi.cc/qqlol?qq=" + lol).sendString();
                } catch (Exception exception) {
                    error(exception.getMessage());
                }

                info(response);
                info = GsonUtils.jsonToBean(response, LOLInfo.class);
                if (info != null) {
                    info("返回状态: " + info.getStatus());
                    info("返回消息: " + info.getMessage());
                    info("QQ: " + info.getQq());
                    info("手机号: " + info.getName());
                    info("地区: " + info.getDaqu());
                }
            };
            new Thread(run).start();
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("lolfc").then(argument("name", StringArgumentType.string()).executes(context -> {
            Runnable run = () -> {
                String lolfc;
                var response = "";
                LOLInfo info;

                lolfc = StringArgumentType.getString(context, "name");
                try {
                    response = Http.get("https://zy.xywlapi.cc/lolname?name=" + lolfc).sendString();
                } catch (Exception exception) {
                    error(exception.getMessage());
                }

                info(response);
                info = GsonUtils.jsonToBean(response, LOLInfo.class);
                if (info != null) {
                    info("返回状态: " + info.getStatus());
                    info("返回消息: " + info.getMessage());
                    info("QQ: " + info.getQq());
                    info("手机号: " + info.getName());
                    info("大区: " + info.getDaqu());
                }
            };
            new Thread(run).start();
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("qq").then(argument("qq-number", StringArgumentType.string()).executes(context -> {
            Runnable run = () -> {
                String sfzNum;
                var response = "";
                QQInfo info;

                sfzNum = StringArgumentType.getString(context, "qq-number");
                try {
                    response = Http.get("https://zy.xywlapi.cc/qqapi?qq=" + sfzNum).sendString();
                } catch (Exception exception) {
                    error(exception.getMessage());
                }

                info(response);
                info = GsonUtils.jsonToBean(response, QQInfo.class);
                if (info != null) {
                    info("返回状态: " + info.getStatus());
                    info("返回消息: " + info.getMessage());
                    info("QQ: " + info.getQq());
                    info("手机号: " + info.getPhone());
                    info("地区: " + info.getPhonediqu());
                }
            };
            new Thread(run).start();
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("qqfc").then(argument("qq-phone", StringArgumentType.string()).executes(context -> {
            Runnable run = () -> {
                String sfzNum;
                var response = "";
                QQInfo info;

                sfzNum = StringArgumentType.getString(context, "qq-phone");
                try {
                    response = Http.get("https://zy.xywlapi.cc/qqphone?phone=" + sfzNum).sendString();
                } catch (Exception exception) {
                    error(exception.getMessage());
                }

                info(response);
                info = GsonUtils.jsonToBean(response, QQInfo.class);
                if (info != null) {
                    info("返回状态: " + info.getStatus());
                    info("返回消息: " + info.getMessage());
                    info("QQ: " + info.getQq());
                    info("手机号: " + info.getPhone());
                    info("地区: " + info.getPhonediqu());
                }
            };
            new Thread(run).start();
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("qqlm").then(argument("qq-lm", StringArgumentType.string()).executes(context -> {
            Runnable run = () -> {
                String sfzNum;
                var response = "";
                QQlmInfo info;

                sfzNum = StringArgumentType.getString(context, "qq-lm");
                try {
                    response = Http.get("https://zy.xywlapi.cc/qqlm?qq=" + sfzNum).sendString();
                } catch (Exception exception) {
                    error(exception.getMessage());
                }

                info(response);
                info = GsonUtils.jsonToBean(response, QQlmInfo.class);
                if (info != null) {
                    info("返回状态: " + info.getStatus());
                    info("返回消息: " + info.getMessage());
                    info("QQ: " + info.getQq());
                    info("QQ老密: " + info.getQqlm());
                }
            };
            new Thread(run).start();
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("wb").then(argument("wb-id", StringArgumentType.string()).executes(context -> {
            Runnable run = () -> {
                String sfzNum;
                var response = "";
                WBInfo info;

                sfzNum = StringArgumentType.getString(context, "wb-id");
                sfzNum = StringArgumentType.getString(context, "qq-lm");
                try {
                    response = Http.get("https://zy.xywlapi.cc/wbapi?id=" + sfzNum).sendString();
                } catch (Exception exception) {
                    error(exception.getMessage());
                }
                info(response);
                info = GsonUtils.jsonToBean(response, WBInfo.class);
                if (info != null) {
                    info("返回状态: " + info.getStatus());
                    info("返回消息: " + info.getMessage());
                    info("ID: " + info.getID());
                    info("手机号: " + info.getPhone());
                    info("地区: " + info.getPhonediqu());
                }
            };
            new Thread(run).start();
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("wbfc").then(argument("wb-phone", StringArgumentType.string()).executes(context -> {
            Runnable run = () -> {
                String sfzNum;
                var response = "";
                WBInfo info;

                sfzNum = StringArgumentType.getString(context, "wb-phone");
                try {
                    response = Http.get("https://zy.xywlapi.cc/wbphone?phone=" + sfzNum).sendString();
                } catch (Exception exception) {
                    error(exception.getMessage());
                }

                info(response);
                info = GsonUtils.jsonToBean(response, WBInfo.class);
                if (info != null) {
                    info("返回状态: " + info.getStatus());
                    info("返回消息: " + info.getMessage());
                    info("ID: " + info.getID());
                    info("手机号: " + info.getPhone());
                    info("地区: " + info.getPhonediqu());
                }
            };
            new Thread(run).start();
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("sfz").then(argument("sfz-hm", StringArgumentType.string()).executes(context -> {
            Runnable run = () -> {
                String sfzNum;
                var response = "";
                SfzInfo info;
                sfzNum = StringArgumentType.getString(context, "sfz-hm");
                try {
                    response = Http.get("http://api.k780.com/?app=idcard.get&idcard=" + sfzNum + "&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json").sendString();
                } catch (Exception exception) {
                    error(exception.getMessage());
                }

                info(response);
                info = GsonUtils.jsonToBean(response, SfzInfo.class);
                if (info != null) {
                    info("success: " + info.getSuccess());
                    info("status: " + info.getResult().getStatus());
                    info("idcard: " + info.getResult().getIdcard());
                    info("par: " + info.getResult().getPar());
                    info("born: " + info.getResult().getBorn());
                    info("sex: " + info.getResult().getSex());
                    info("att: " + info.getResult().getAtt());
                    info("postno: " + info.getResult().getPostno());
                    info("areano: " + info.getResult().getAreano());
                    info("style_simcall: " + info.getResult().getStyle_simcall());
                    info("style_citynm: " + info.getResult().getStyle_citynm());
                    info("msg: " + info.getResult().getMsg());
                }
            };
            new Thread(run).start();
            return SINGLE_SUCCESS;
        })));
    }

    @Getter
    public static class SfzInfo {
        String success;
        Result result;

        public SfzInfo(String success, Result result) {
            this.success = success;
            this.result = result;
        }

        public void setSuccess(String success) {
            this.success = success;
        }

        public void setResult(Result result) {
            this.result = result;
        }
    }

    @Setter
    public static class WBInfo {
        String status;
        String message;
        String id;
        String phone;
        String phonediqu;

        public WBInfo(String status, String message, String id, String phone, String phonediqu) {
            this.status = status;
            this.message = message;
            this.id = id;
            this.phone = phone;
            this.phonediqu = phonediqu;
        }

        public String getStatus() {
            return this.status;
        }

        public String getMessage() {
            return this.message;
        }

        public String getID() {
            return this.id;
        }

        public String getPhone() {
            return this.phone;
        }

        public String getPhonediqu() {
            return this.phonediqu;
        }

    }

    @Getter
    public static class Result {
        String status;
        String par;
        String idcard;
        String born;
        String sex;
        String att;
        String postno;
        String areano;
        String style_simcall;
        String style_citynm;
        String msg;

        public Result(String status, String idcard, String par, String born, String sex, String att, String postno, String areano, String style_simcall, String style_citynm, String msg) {
            this.status = status;
            this.idcard = idcard;
            this.par = par;
            this.born = born;
            this.sex = sex;
            this.att = att;
            this.postno = postno;
            this.areano = areano;
            this.style_simcall = style_simcall;
            this.style_citynm = style_citynm;
            this.msg = msg;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setIdcard(String idcard) {
            this.idcard = idcard;
        }

        public void setPar(String par) {
            this.par = par;
        }

        public void setBorn(String born) {
            this.born = born;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public void setAtt(String att) {
            this.att = att;
        }

        public void setPostno(String postno) {
            this.postno = postno;
        }

        public void setAreano(String areano) {
            this.areano = areano;
        }

        public void setStyle_simcall(String style_simcall) {
            this.style_simcall = style_simcall;
        }

        public void setStyle_citynm(String style_citynm) {
            this.style_citynm = style_citynm;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

    }

    public static class QQlmInfo {
        String status;
        String message;
        String qq;
        String qqlm;

        public QQlmInfo(String status, String message, String qq, String qqlm) {
            this.status = status;
            this.message = message;
            this.qq = qq;
            this.qqlm = qqlm;
        }

        public String getStatus() {
            return this.status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMessage() {
            return this.message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getQq() {
            return this.qq;
        }

        public void setQq(String qq) {
            this.qq = qq;
        }

        public String getQqlm() {
            return this.qqlm;
        }

        public void setQqlm(String qqlm) {
            this.qqlm = qqlm;
        }
    }

    public static class QQInfo {
        String status;
        String message;
        String qq;
        String phone;
        String phonediqu;

        public QQInfo(String status, String message, String qq, String phone, String phonediqu) {
            this.status = status;
            this.message = message;
            this.qq = qq;
            this.phone = phone;
            this.phonediqu = phonediqu;
        }

        public String getStatus() {
            return this.status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMessage() {
            return this.message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getQq() {
            return this.qq;
        }

        public void setQq(String qq) {
            this.qq = qq;
        }

        public String getPhone() {
            return this.phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPhonediqu() {
            return this.phonediqu;
        }

        public void setPhonediqu(String phonediqu) {
            this.phonediqu = phonediqu;
        }
    }

    public static class LOLInfo {
        String status;
        String message;
        String qq;
        String name;
        String daqu;

        public LOLInfo(String status, String message, String qq, String name, String daqu) {
            this.status = status;
            this.message = message;
            this.qq = qq;
            this.name = name;
            this.daqu = daqu;
        }

        public String getStatus() {
            return this.status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMessage() {
            return this.message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getQq() {
            return this.qq;
        }

        public void setQq(String qq) {
            this.qq = qq;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDaqu() {
            return this.daqu;
        }

        public void setDaqu(String daqu) {
            this.daqu = daqu;
        }
    }
}
