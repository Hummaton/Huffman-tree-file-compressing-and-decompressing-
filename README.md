# Huffman-tree-file-compressing-and-decompressing-

Part I: This program will compress a source file into a target file using the Huffman coding method. First, ObjectOutputStream is used to output the Huffman
codes into the target file, then BitOutputStream is used to output the encoded binary contents to the target file. The files are passed from the
command line using something like the following command:
C:\Users\.... > java Compress_a_File sourceFile.txt compressedFile.txt
When I did this for my source file, I got a compressed file that looked something like the following:
¬í w Zur [Ljava.lang.String;ÒVçé{G xp ppppppppppt 01101ppt 11010ppppppppppppppppppt 100pppppppppppt 10101pt 1111010ppppppppppppppppppt
011000ppppppppppppppppt 1010011pt 1111011pt 1101110ppppppppppt 110110t 1101111t 00000t 01111t 010t 101000pt 00001t 11111ppt 01110t 00010t
00011t 1110t 111100pt 1011t 001t 1100t 011001pppt
1010010pppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp
û§Åµ©iõÓwœ°ÍMîeWM÷t¦tËDÌÌ£îUÓXæ=…å¢gògw

Part II: In the second program, Decompress_a_File.java, the file will be decompressed (a previously compressed file) so that it replicates the original source file in Part I
above. You would do this at the command line with a command that looks like the following:
C:\Users\...> java Decompress_a_File compressedFile.txt decompressedFile.txt

When I ran this second program on my compressed file from Part I above I got:
Roses are red,
Violets are blue,
Try to compress this file,
And then decompress it too.
