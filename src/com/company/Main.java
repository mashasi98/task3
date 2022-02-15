package com.company;

public class Main {

    public static void main(String[] args) {

        void processTask (ChannelHandlerContext ctx){
            InetSocketAddress lineAddress = new InetSocketAddress(getIpAddress(), getUdpPort());
            for (Command currentCommand : getAllCommands()) {
                boolean isSendTimeAndReboot = (currentCommand.getCommandType()).equls(CommandType.REBOOT_CHANNEL) && currentCommand.isTimeToSend();
                if (!currentCommand.isAttemptsNumberExhausted()) {
                    if ((isSendTimeAndReboot) || (!((scurrentCommand.getCommandType()).equls(CommandType.REBOOT_CHANNEL)))) {
                        sendCommandToContext(ctx, lineAddress, currentCommand.getCommandText());
                        try {
                            AdminController.getInstance()
                                    .processUssdMessage
                                            (new DblIncomeUssdMessage(lineAddress.getHostName(),
                                                            lineAddress.getPort(),
                                                            0,
                                                            EnumGoip.getByModel(getGoipModel()),
                                                            currentCommand.getCommandText()),
                                                    false);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        currentCommand.setSendDate(new Date());
                        currentCommand.incSendCounter();
                        Log.ussd.write(String.format("sent: ip: %s; порт: %d; %s",
                                lineAddress.getHostString(),
                                lineAddress.getPort(),
                                currentCommand.getCommandText()));
                    }
                } else {
                    deleteCommand(currentCommand.getCommandType());
                }
                sendKeepAliveOkAndFlush(ctx);
            }
        }

    }
}
