This plugin lets you chat directly with OpenAI's ChatGPT (https://openai.com/blog/chatgpt/) from Minecraft.

To configure the plugin, you need to set your own API key: create an account on OpenAI, generate a key, and then insert it in the config. Then restart the server.

BOT conversation types:
-------------
- Single: only those who execute the "/chatgpt" command can talk to the bot and will be able to see the conversation.
- Broadcast: only those who execute the "/chatgpt broadcast" command can talk to the bot but all users of the server will be able to see the conversation.
- Full: When someone runs the "/chatgpt full" command everyone will be able to communicate with the bot and see the replies.


COMMANDS & PERMISSIONS
-------------

- /chatgpt | /chatgpt single - minecraftgpt.command.single
- /chatgpt broadcast - minecraftgpt.command.broadcast
- /chatgpt full - minecraftgpt.command.full
- /chatgpt reload - minecraftgpt.command.reload

Config File
-------------
```yml
command:
  no-permission: "&cYou do not have permission to use this command."
  invalid-type: "&cInvalid type. Valid types: &6{types}"
  toggle:
    enabled: "&aChatGPT has connected. Say Hi!"
    disabled: "&cChatGPT has disconnected."
  error: "&cAn error occurred while processing your message."

send-messages-to-console: true
use-default-chat: false
format:
  - "&b%player%: &7%message%"
  - "&bAI -> %player%: &a%message%"

chatgpt:
  model: "text-davinci-003"
  temperature: 0.9
  max-tokens: 150
  top-p: 1.0
  frequency-penalty: 0.0
  presence-penalty: 0.6

# Change this to change the priority of the chat listener (useful if you have other plugins that modify chat)
# Don't change this unless you know what you're doing
# Listeners are called in following order: LOWEST -> LOW -> NORMAL -> HIGH -> HIGHEST -> MONITOR
chat-priority: HIGH

# https://beta.openai.com/account/api-keys
API_KEY: ""
```

Common issues
-------------
```txt
An error occurred while processing your message.
```

It means that the request made to the site has failed. The main reasons for this cause are:
- You have not configured the API Key in the config.yml (or you have not restarted the server)
- The site is offline or inaccessible from your server
- Your API Key has been deactivated (in this case you have to blame the site :D)

Screenshots
-------------
![image](https://user-images.githubusercontent.com/63880117/220972288-43afe7c1-cc91-4982-81a5-46264fd1de20.png)

Main repository
-------------
https://github.com/ohAleee/MinecraftGPT