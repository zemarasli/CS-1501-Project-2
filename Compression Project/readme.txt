//this is where my compression/expansion evaluation of the 14 files will go using:
        - unmodified LZW.java
        - MyLZW.java using do nothing mode
        - MyLZW.java using reset mode
        - MyLZW.java using monitor mode
        - another existing compression application
  //measure the original file size
                compressed file size
                compression ratio (original file size / compressed file size) when compressed


File Name           Original File Size        Compressed file size // compression ratio (LZW.java)         Compressed file size // compression ratio (MyLZW - n)      Compressed file size // compression ratio (MyLZW - r)      Compressed file size // compression ratio (MyLZW - m)      Compressed file size // compression ratio (gzip)
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
all.tar                   3 MB                             1.8 MB // 1.67                                               1.8 MB // 1.67                                              1.2 MB // 2.50                                            1.8 MB // 1.67                                          969 KB // 3.09

assig2.doc                115 KB                            66 KB // 1.74                                               34 KB // 3.38                                               34 KB // 3.38                                             34 KB // 3.38                                           20 KB // 5.75

bmps.tar                  1.1 MB                            925 KB // 1.19                                              81 KB // 13.58                                              81 KB // 13.58                                            81 KB // 13.58                                          64 KB // 17.18

code2.txt                 53 KB                             24 KB // 2.20                                               20 KB //  2.65                                              20 KB //  2.65                                            20 KB //  2.65                                          13 KB // 4.07

code.txt                  69 KB                             30 KB // 2.30                                               8 KB //  8.63                                               24 KB // 2.87                                             24 KB // 2.87                                           15 KB // 4.60

edit.exe                  236 KB                            251 KB // 0.94                                              156 KB // 1.51                                              152 KB // 1.55                                            156 KB // 1.51                                          66 KB // 3.57

frosty.jpg                127 KB                            177 KB // 0.72                                              164 KB // 0.77                                              171 KB // 0.74                                            164 KB // 0.77                                          127 KB // 1.00

gone_fishing.bmp          17 KB                             9 KB // 1.89                                                9 KB // 1.89                                                9 KB // 1.89                                              9 KB // 1.89                                            9 KB // 1.89

large.txt                 1.2 MB                            599 KB // 2.00                                              498 KB // 2.41                                              524 MB // 2.29                                            498 KB // 2.41                                          328 KB // 3.66

Lego-big.gif              93 KB                             129 KB // 0.72                                              122 KB // 0.76                                              122 KB // 0.76                                            122 KB // 0.76                                          92 KB // 1.01

medium.txt                25 KB                             13 KB // 1.92                                               12 KB // 2.08                                               12 KB // 2.08                                             12 KB // 2.08                                           11 KB // 2.27

texts.tar                 1.4 MB                            1 MB // 1.4                                                 598 KB // 2.34                                              591 MB // 2.37                                            598 MB // 2.34                                          533 KB // 2.62

wacky.bmp                 922 KB                            4 KB // 230.50                                              4 KB // 230.50                                              4 KB // 230.50                                            4 KB // 230.50                                          3 KB // 307.33

winnt256.bmp              157 KB                            159 KB // 0.98                                              63 KB // 2.49                                               63 KB // 2.49                                             63 KB // 2.49                                           50 KB // 3.18
