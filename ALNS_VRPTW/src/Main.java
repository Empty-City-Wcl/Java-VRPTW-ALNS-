import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        long begintime = System.nanoTime();

        ReadTXT.importFileInfo("c101",                    //输入想要测试的文件名称
                "solomon"                                       //输入想要测试的算例名称(solomon、homberger)
        );
        ReadTXT.InitNodeandRoute();                             //初始化节点、路线以及最优路线指标
        try {
            ReadTXT.importInfo();
        } catch (Exception e2) {
            e2.printStackTrace();
        }

        Calculation.CalDistanceMatrix();                        //生成初始距离矩阵
        Calculation.CalRelatedMatrix();                         //生成节点相关性矩阵
        ReadTXT.ExtracRoutes();                                 //生成初始解路线

        //初始化破坏算子和修复算子的相关参数
        for (int i = 0; i < 2;i++) {
            Parameter.WRepair[i] = 1;
            Parameter.WDestory[i] = 1;
            Parameter.DestoryUseTime[i] = 0;
            Parameter.RepairUseTime[i] = 0;
        }

        //算法实现
        ALNS.ALNS();
        //结果输出
        OutPut.Output();

        long endtime = System.nanoTime();
        double usedTime = (endtime - begintime) / (1e9);
        System.out.println();
        System.out.println("程序耗时：" + usedTime + "s");

    }
}