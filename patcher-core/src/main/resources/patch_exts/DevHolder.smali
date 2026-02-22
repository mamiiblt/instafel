.class public Linstafel/app/utils/DevHolder;
.super Ljava/lang/Object;
.source "DevHolder.java"


# static fields
.field private static activity:Lcom/instagram/mainactivity/InstagramMainActivity;

.field private static userSession:Lcom/instagram/common/session/UserSession;

# direct methods
.method private constructor <init>()V
    .registers 1

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static getActivity()Lcom/instagram/mainactivity/InstagramMainActivity;
    .registers 1

    sget-object v0, Linstafel/app/utils/DevHolder;->activity:Lcom/instagram/mainactivity/InstagramMainActivity;

    return-object v0
.end method

.method public static getSession()Lcom/instagram/common/session/UserSession;
    .registers 1

    sget-object v0, Linstafel/app/utils/DevHolder;->userSession:Lcom/instagram/common/session/UserSession;

    return-object v0
.end method

.method public static set(Lcom/instagram/mainactivity/InstagramMainActivity;Lcom/instagram/common/session/UserSession;)V
    .registers 2

    sput-object p0, Linstafel/app/utils/DevHolder;->activity:Lcom/instagram/mainactivity/InstagramMainActivity;

    sput-object p1, Linstafel/app/utils/DevHolder;->userSession:Lcom/instagram/common/session/UserSession;

    return-void
.end method
