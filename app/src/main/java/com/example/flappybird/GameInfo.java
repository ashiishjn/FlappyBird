package com.example.flappybird;

public class GameInfo {

    public static int character_selected = 0;
    public static int character_info[] = {1,0,0,0};
    public static int character_purchase_price[] = {0,5151,10101,15151};

    public static int background_selected = 0;
    public static int background_info[] = {1,0,0};
    public static int background_purchase_price[] = {0,10101,30303};

    public static int sound_selected = 0;
    public static int sound_info[] = {
                            1,0,0,0,0,0,0,0,0,0,
                            0,0,0,0,0,0,0,0,0,0,
                            0,0,0,0,0,0,0,0,0,0,
                            0,0,0,0,0,0,0,0,0,0,
                            0,0,0,0,0,0,0,0,0,0,
                            0,0,0,0,0,0,0,0,0,0
                        };

    public static int sound_listen_ad[] = {
                            1,0,0,0,0,0,0,0,0,0,
                            0,0,0,0,0,0,0,0,0,0,
                            0,0,0,0,0,0,0,0,0,0,
                            0,0,0,0,0,0,0,0,0,0,
                            0,0,0,0,0,0,0,0,0,0,
                            0,0,0,0,0,0,0,0,0,0
                        };

    public static int highestScores;

    public static int elixir;

    public static boolean guestUser = false;

    public static int totalLife = 2;

    public static boolean volume = true;

    public static int sounds[] = {
            R.raw.s1, R.raw.s2, R.raw.s3, R.raw.s4, R.raw.s5, R.raw.s6, R.raw.s7, R.raw.s8, R.raw.s9, R.raw.s10,
            R.raw.s11,R.raw.s12,R.raw.s13,R.raw.s14,R.raw.s15,R.raw.s16,R.raw.s17,R.raw.s18,R.raw.s19,R.raw.s20,
            R.raw.s21,R.raw.s22,R.raw.s23,R.raw.s24,R.raw.s25,R.raw.s26,R.raw.s27,R.raw.s28,R.raw.s29,R.raw.s30,
            R.raw.s31,R.raw.s32,R.raw.s33,R.raw.s34,R.raw.s35,R.raw.s36,R.raw.s37,R.raw.s38,R.raw.s39,R.raw.s40,
            R.raw.s41,R.raw.s42,R.raw.s43,R.raw.s44,R.raw.s45,R.raw.s46,R.raw.s47,R.raw.s48,R.raw.s49,R.raw.s50,
            R.raw.s51,R.raw.s52,R.raw.s53,R.raw.s54,R.raw.s55,R.raw.s56,R.raw.s57,R.raw.s58,R.raw.s59,R.raw.s60
    };

    public static int sound_selected_img[] = {
            R.drawable.music_selected,R.drawable.music_selected,R.drawable.music_selected,R.drawable.music_selected,
            R.drawable.music_selected,R.drawable.music_selected,R.drawable.music_selected,R.drawable.music_selected,
            R.drawable.music_selected,R.drawable.music_selected,R.drawable.music_selected,R.drawable.music_selected,
            R.drawable.music_selected,R.drawable.music_selected,R.drawable.music_selected,R.drawable.music_selected,
            R.drawable.music_selected,R.drawable.music_selected,R.drawable.music_selected,R.drawable.music_selected,
            R.drawable.music_selected,R.drawable.music_selected,R.drawable.music_selected,R.drawable.music_selected,
            R.drawable.music_selected,R.drawable.music_selected,R.drawable.music_selected,R.drawable.music_selected,
            R.drawable.music_selected,R.drawable.music_selected,R.drawable.music_selected,R.drawable.music_selected,
            R.drawable.music_selected,R.drawable.music_selected,R.drawable.music_selected,R.drawable.music_selected,
            R.drawable.music_selected,R.drawable.music_selected,R.drawable.music_selected,R.drawable.music_selected,
            R.drawable.music_selected,R.drawable.music_selected,R.drawable.music_selected,R.drawable.music_selected,
            R.drawable.music_selected,R.drawable.music_selected,R.drawable.music_selected,R.drawable.music_selected,
            R.drawable.music_selected,R.drawable.music_selected,R.drawable.music_selected,R.drawable.music_selected,
            R.drawable.music_selected,R.drawable.music_selected,R.drawable.music_selected,R.drawable.music_selected,
            R.drawable.music_selected,R.drawable.music_selected,R.drawable.music_selected,R.drawable.music_selected
    };

    public static int sound_Purchased_img[] = {
            R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,
            R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,
            R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,
            R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,
            R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,
            R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,
            R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,
            R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,
            R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,
            R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,
            R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,
            R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music,R.drawable.music
    };

    public static int background_Purchased_img[] = {
            R.drawable.forest,  R.drawable.sunnyday, R.drawable.underground
    };

    public static int character_Purchased_img[] = {
            R.drawable.a1, R.drawable.a2, R.drawable.a3, R.drawable.a4, R.drawable.a5, R.drawable.a6
    };

    public static String sound_GamePlay[] = {
            "s1.mp3", "s2.mp3", "s3.mp3", "s4.mp3", "s5.mp3", "s6.mp3", "s7.mp3", "s8.mp3", "s9.mp3", "s10.mp3",
            "s11.mp3","s12.mp3","s13.mp3","s14.mp3","s15.mp3","s16.mp3","s17.mp3","s18.mp3","s19.mp3","s20.mp3",
            "s21.mp3","s22.mp3","s23.mp3","s24.mp3","s25.mp3","s26.mp3","s27.mp3","s28.mp3","s29.mp3","s30.mp3",
            "s31.mp3","s32.mp3","s33.mp3","s34.mp3","s35.mp3","s36.mp3","s37.mp3","s38.mp3","s39.mp3","s40.mp3",
            "s41.mp3","s42.mp3","s43.mp3","s44.mp3","s45.mp3","s46.mp3","s47.mp3","s48.mp3","s49.mp3","s50.mp3",
            "s51.mp3","s52.mp3","s53.mp3","s54.mp3","s55.mp3","s56.mp3","s57.mp3","s58.mp3","s59.mp3","s60.mp3",
    };

    public static String background_top[] = {
            "forest_top.png", "sunnyDay_top.png", "underground_top.png"
    };

    public static String background_base[] = {
            "forest_base.png","sunnyDay_base.png","underground_base.png"
    };

}