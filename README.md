# EcsAutoDeletion

通过此插件，你可以实现

> 当服务器人数为 0 人超过 *n* 秒时，删除当前阿里云 ECS 实例。

适用场景是抢占式实例。如果你希望创建一个「玩家少了自动关服以节省金钱消耗（必须删除实例才会停止计费），玩家想玩可以自主开服」的游戏模式，那么该插件可以帮助你完成自动关服的一环。

## 构建

```shell
git clone https://github.com/sotapmc/EcsAutoDeletion.git
cd EcsAutoDeletion
./gradlew build
# jar 会生成在 build/libs 里。
```

## 配置文件

```yml
# 必填 - 阿里云 AccessKeyId
access_key_id: ~
# 必填 - 阿里云 AccessKeySecret
access_key_secret: ~
# 必填 - 允许服务器空闲的最大时间，单位为秒
max_empty_time: 43200
# 必填 - 备份脚本在服务器上的位置，若不需要备份请写 "~"
backup_script_path: ~
# 必填 - 主机所在地域
region: cn-shanghai
# 必填 - 主机名，可在 ECS 控制台查看
instance_name: ~
```

所有的项目都必填，没有正确填写将导致插件无法正常运行。阿里云的 `AccessKeyId` 和 `AccessKeySecret` 所对应的 RAM 账户必须具有 ECS 操作方面的权限。

## 协议

MIT
