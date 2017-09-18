package com.spriton.therapypi;

import java.util.*;

public class KneeAngleLookup {

    private static Map<Integer, Double> lookupChart = new HashMap<>();

    public static int getKneeAngle(double shaftAngle) {
        return getKneeAngle((int) Math.round(shaftAngle));
    }

    public static int getKneeAngle(int shaftAngle) {
        Double result = lookupChart.get(shaftAngle);
        return (int) (result != null ? Math.round(result) : shaftAngle);
    }

    static {
        lookupChart.put(-20, -6.4);
        lookupChart.put(-19, -6.2);
        lookupChart.put(-18, -6.0);
        lookupChart.put(-17, -5.9);
        lookupChart.put(-16, -5.6);
        lookupChart.put(-15, -5.4);
        lookupChart.put(-14, -5.3);
        lookupChart.put(-13, -5.1);
        lookupChart.put(-12, -4.9);
        lookupChart.put(-11, -4.7);
        lookupChart.put(-10, -4.6);
        lookupChart.put(-9, -4.5);
        lookupChart.put(-8, -4.2);
        lookupChart.put(-7, -4.1);
        lookupChart.put(-6, -3.9);
        lookupChart.put(-5, -3.8);
        lookupChart.put(-4, -2.5);
        lookupChart.put(-3, -1.5);
        lookupChart.put(-2, -0.3);
        lookupChart.put(-1,	1.8);
        lookupChart.put(0, 2.4);
        lookupChart.put(1, 3.0);
        lookupChart.put(2, 4.0);
        lookupChart.put(3, 5.0);
        lookupChart.put(4, 5.5);
        lookupChart.put(5, 6.4);
        lookupChart.put(6, 7.0);
        lookupChart.put(7, 7.7);
        lookupChart.put(8, 8.2);
        lookupChart.put(9, 9.0);
        lookupChart.put(10, 10.0);
        lookupChart.put(11, 10.8);
        lookupChart.put(12, 11.6);
        lookupChart.put(13, 12.5);
        lookupChart.put(14, 13.4);
        lookupChart.put(15, 14.4);
        lookupChart.put(16, 15.3);
        lookupChart.put(17, 16.5);
        lookupChart.put(18, 17.5);
        lookupChart.put(19, 18.5);
        lookupChart.put(20, 19.6);
        lookupChart.put(21, 20.5);
        lookupChart.put(22, 21.6);
        lookupChart.put(23, 22.7);
        lookupChart.put(24, 23.8);
        lookupChart.put(25, 25.0);
        lookupChart.put(26, 26.0);
        lookupChart.put(27, 27.0);
        lookupChart.put(28, 28.0);
        lookupChart.put(29, 29.0);
        lookupChart.put(30, 30.0);
        lookupChart.put(31, 31.0);
        lookupChart.put(32, 32.0);
        lookupChart.put(33, 33.0);
        lookupChart.put(34, 34.0);
        lookupChart.put(35, 35.0);
        lookupChart.put(36, 36.0);
        lookupChart.put(37, 37.0);
        lookupChart.put(38, 38.0);
        lookupChart.put(39, 39.0);
        lookupChart.put(40, 40.0);
        lookupChart.put(41, 41.0);
        lookupChart.put(42, 42.0);
        lookupChart.put(43, 43.0);
        lookupChart.put(44, 44.0);
        lookupChart.put(45, 45.0);
        lookupChart.put(46, 46.0);
        lookupChart.put(47, 47.0);
        lookupChart.put(48, 48.0);
        lookupChart.put(49, 49.0);
        lookupChart.put(50, 50.0);
        lookupChart.put(51, 51.0);
        lookupChart.put(52, 52.0);
        lookupChart.put(53, 53.0);
        lookupChart.put(54, 54.0);
        lookupChart.put(55, 55.0);
        lookupChart.put(56, 56.0);
        lookupChart.put(57, 57.0);
        lookupChart.put(58, 58.0);
        lookupChart.put(59, 59.0);
        lookupChart.put(60, 60.0);
        lookupChart.put(61, 61.0);
        lookupChart.put(62, 62.0);
        lookupChart.put(63, 63.0);
        lookupChart.put(64, 64.0);
        lookupChart.put(65, 65.0);
        lookupChart.put(66, 66.0);
        lookupChart.put(67, 67.0);
        lookupChart.put(68, 68.0);
        lookupChart.put(69, 69.0);
        lookupChart.put(70, 70.0);
        lookupChart.put(71, 71.0);
        lookupChart.put(72, 72.0);
        lookupChart.put(73, 73.0);
        lookupChart.put(74, 74.0);
        lookupChart.put(75, 75.0);
        lookupChart.put(76, 76.0);
        lookupChart.put(77, 77.0);
        lookupChart.put(78, 78.0);
        lookupChart.put(79, 79.0);
        lookupChart.put(80, 80.0);
        lookupChart.put(81,	80.9);
        lookupChart.put(82,	81.8);
        lookupChart.put(83,	82.8);
        lookupChart.put(84,	83.8);
        lookupChart.put(85,	84.6);
        lookupChart.put(86,	85.7);
        lookupChart.put(87,	86.7);
        lookupChart.put(88,	87.7);
        lookupChart.put(89,	88.6);
        lookupChart.put(90,	89.2);
        lookupChart.put(91,	90.0);
        lookupChart.put(92,	90.8);
        lookupChart.put(93,	91.6);
        lookupChart.put(94,	92.3);
        lookupChart.put(95,	93.0);
        lookupChart.put(96,	93.5);
        lookupChart.put(97,	94.2);
        lookupChart.put(98,	95.0);
        lookupChart.put(99,	95.8);
        lookupChart.put(100, 96.4);
        lookupChart.put(101, 97.2);
        lookupChart.put(102, 97.9);
        lookupChart.put(103, 98.8);
        lookupChart.put(104, 99.8);
        lookupChart.put(105, 100.8);
        lookupChart.put(106, 101.2);
        lookupChart.put(107, 101.8);
        lookupChart.put(108, 102.7);
        lookupChart.put(109, 103.2);
        lookupChart.put(110, 104.0);
        lookupChart.put(111, 104.6);
        lookupChart.put(112, 105.3);
        lookupChart.put(113, 106.1);
        lookupChart.put(114, 106.8);
        lookupChart.put(115, 107.4);
        lookupChart.put(116, 107.9);
        lookupChart.put(117, 108.3);
        lookupChart.put(118, 108.8);
        lookupChart.put(119, 109.3);
        lookupChart.put(120, 110.0);
        lookupChart.put(121, 110.5);
        lookupChart.put(122, 111.0);
        lookupChart.put(123, 111.5);
        lookupChart.put(124, 112.0);
        lookupChart.put(125, 112.8);
        lookupChart.put(126, 113.0);
        lookupChart.put(127, 113.3);
        lookupChart.put(128, 113.8);
        lookupChart.put(129, 114.2);
        lookupChart.put(130, 114.6);
        lookupChart.put(131, 115.0);
        lookupChart.put(132, 115.5);
        lookupChart.put(133, 116.0);
        lookupChart.put(134, 116.4);
        lookupChart.put(135, 117.0);
        lookupChart.put(136, 117.3);
        lookupChart.put(137, 117.8);
        lookupChart.put(138, 118.2);
        lookupChart.put(139, 118.6);
        lookupChart.put(140, 119.0);
        lookupChart.put(141, 119.5);
        lookupChart.put(142, 119.9);
        lookupChart.put(143, 120.4);
        lookupChart.put(144, 120.9);
        lookupChart.put(145, 121.4);
        lookupChart.put(146, 121.9);
        lookupChart.put(147, 122.2);
        lookupChart.put(148, 122.7);
        lookupChart.put(149, 123.0);
        lookupChart.put(150, 123.4);
        lookupChart.put(151, 123.8);
        lookupChart.put(152, 124.2);
        lookupChart.put(153, 124.8);
        lookupChart.put(154, 125.2);
        lookupChart.put(155, 125.8);
        lookupChart.put(156, 126.0);
        lookupChart.put(157, 126.6);
        lookupChart.put(158, 127.2);
        lookupChart.put(159, 127.8);
        lookupChart.put(160, 128.4);
        lookupChart.put(161, 128.8);
        lookupChart.put(162, 129.2);
        lookupChart.put(163, 129.8);
        lookupChart.put(164, 130.2);
        lookupChart.put(165, 130.6);
        lookupChart.put(166, 131.2);
        lookupChart.put(167, 131.8);
        lookupChart.put(168, 132.3);
        lookupChart.put(169, 132.9);
        lookupChart.put(170, 133.5);
        lookupChart.put(171, 134.1);
        lookupChart.put(172, 134.7);
        lookupChart.put(173, 135.3);
        lookupChart.put(174, 135.9);
        lookupChart.put(175, 136.5);
        lookupChart.put(176, 137.1);
        lookupChart.put(177, 137.7);
        lookupChart.put(178, 137.3);
        lookupChart.put(179, 137.9);
        lookupChart.put(180, 138.5);
        lookupChart.put(181, 139.1);
        lookupChart.put(182, 139.7);
        lookupChart.put(183, 140.3);
        lookupChart.put(184, 141.9);
        lookupChart.put(185, 142.5);
        lookupChart.put(186, 143.1);
        lookupChart.put(187, 143.7);
        lookupChart.put(188, 144.3);
        lookupChart.put(189, 144.9);
        lookupChart.put(190, 145.5);
        lookupChart.put(191, 146.1);
        lookupChart.put(192, 146.7);
        lookupChart.put(193, 147.3);
        lookupChart.put(194, 147.9);
        lookupChart.put(195, 148.5);
    }

}
