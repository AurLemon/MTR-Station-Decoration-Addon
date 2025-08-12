# MTR-Station-Decoration-Addon

[中文](README_zh.md)

A Station Decoration Addon for the Minecraft Transit Railway Mod, This is a fork from [AIDA64S/MTR-Station-Decoration-Addon](https://github.com/AIDA64S/MTR-Station-Decoration-Addon).

We (or more accurately, Claude & Augment Agent, thanks to AI) tried our best to make it **compatible with MTR 3.2.2 and Minecraft 1.20.1 version** in Mod v1.3.4.

If you found any bugs, please report it to us not the original repository. We will fix it as soon as possible (If we could).

Welcome PR! And we are also trying to do other works, like **London Underground MTR Addon** compatible with MTR 3.2.2 and Minecraft 1.20.1 version.

## Branch

Cause original repository has three branches `master`, `1.3.5` and `4.0.0`, the relation between them is: `master` → `1.3.5` → `4.0.0`.

But the detail commit may confused us if we want to modified, you may not sure what's relation between `1.3.5` and `4.0.0`. So we refactor branch structure, the description is as follows:

| Branch         | Supported MTR Version | Supported Minecraft Version | Mod Version                  | Original branch                                                                                                                                                                                                                |
| -------------- | --------------------- | --------------------------- | ---------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| mtr-3.2.2/1.20 | 3.2.2                 | 1.20.1                      | 1.3.4                        | Branched from original repository `master` (commit `142ba1`)                                                                                                                                                                   |
| mtr-3.2.2/1.16 | 3.2.2                 | [1.16.5, 1.19.4]            | 1.3.4                        | Branched from original repository `master` (commit `142ba1`), and this branch is unneed to modify, so still stay in `142ba1`                                                                                                   |
| mtr-4.0.0      | 4.0.0                 | 1.20.1                      | latest (original repository) | Branched from original repository `1.3.5` (commit `faeebe`), then squashed and applied the latest state from original repository `4.0.0` branch as a single commit, bypassing all intermediate commits between 1.3.5 and 4.0.0 |

(Though we tried our best to make it readable, it may not suit for everyone... we feel sorry here)

We also active GitHub Actions, you can download build files from here (Release is also available).

## Compatibility

You may wonder how we made it compatible with MTR 3.2.2 and Minecraft 1.20.1 version. It's not complicated but with a large amount of work. First, we modified Minecraft API calls to adapt 1.20 version, such as Block Properties API (Material to MapColor). Second we found that original rendering methods is not exist, so we try to use MTR API to render. Also, we found the same problem (methods calls) and one by one fixed. The most work is to change calling methods, and find new methods to replace. Finally, we modified build files and run test, and it works! But we found that some features are not work, so we need to continuously fix.

Now, we've tested in upgrading from 1.18.2 to 1.20.1 Forge servers, it's work perfectly (**probably**, so we need you to report bugs if exist). But we aren't completely sure it's almost approach original, so we recommend to test it in your server, and **backup level before upgrade**.

## Setup

1. Clone this repository
2. Execute `./gradlew build`
3. The mod jar file will be in `build/release` directory

If you meet network problem (especially Mainland China), try configure proxy.

If you are using Clash as proxy, you can set proxy in follows, add it to `gradle.properties` file. You can also switch on TUN Mode, *but we recommend to keep it off*, just set System Proxy and set HTTP Proxy below is enough. (TUN Mode may not perfectly work in gradle libraries download, but configure proxy below is always work, so we recommend to keep it off)

```
# HTTP Proxy
# Clash default port is 7890, if you switch on random port or set another port, change it
systemProp.http.proxyHost=127.0.0.1
systemProp.http.proxyPort=7890
systemProp.https.proxyHost=127.0.0.1
systemProp.https.proxyPort=7890
```

## License

[MIT License](https://raw.githubusercontent.com/Hydroline/MTR-Station-Decoration-Addon/master/LICENSE)
