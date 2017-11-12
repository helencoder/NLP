/*
 * Copyright (C) 2007 by
 * 
 * 	Xuan-Hieu Phan
 *	hieuxuan@ecei.tohoku.ac.jp or pxhieu@gmail.com
 * 	Graduate School of Information Sciences
 * 	Tohoku University
 * 
 *  Cam-Tu Nguyen
 *  ncamtu@gmail.com
 *  College of Technology
 *  Vietnam National University, Hanoi
 *
 * JGibbsLDA is a free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * JGibbsLDA is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JGibbsLDA; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package com.thirdparty.lda;

import org.kohsuke.args4j.*;

/**
 * 此处运行需要添加args命令,示例如下:
 *  -est -alpha 0.2 -beta 0.1 -ntopics 100 -niters 1000 -savestep 100 -twords 100 -dir  models\casestudy-en -dfile "newdocs.dat"
 *  具体参数含义,可参见LdaTrain文件
 *  JGibbLDA的参数基本上有下面几个：
 *
 * 	#-est 此参数初次训练LDA模型;
 * 	#-estc:此参数用于从已有的模型当中训练LDA模型;
 * 	#-inf: 此参数用于对新的文档进行测试;
 * 	#-model <string>: 已有的模型的名称;
 * 	#-alpha<double>:α超参数，默认是50/K，K为主题数量;
 * 	#-beta <double>: β超参数，默认为0.1;
 * 	#-ntopics <int>: 主题数量，默认100;
 * 	#-niters <int>: Gibbs抽样迭代次数，默认为2000;
 * 	#-savestep <int>: 保持模型的迭代次数，即每迭代多少次将保持一次结果模型。默认为200;
 * 	#-twords <int>: 每个主题最可能的单车数量。默认为0，如果设定大于0，比如20，JGibbLDA将会根据此参数在每次保持模型时为每个主题打印出最可能的20个词;
 * 	#-dir <string>: 输入词集文件夹目录;此目录也是模型结果输出目录
 * 	#-dfile <string>: 输入词集文件名称
 */
public class LDA {
	
	public static void main(String args[]){
		LDACmdOption option = new LDACmdOption();
		CmdLineParser parser = new CmdLineParser(option);
		
		try {
			if (args.length == 0){
				showHelp(parser);
				return;
			}
			
			parser.parseArgument(args);
			
			if (option.est || option.estc){
				Estimator estimator = new Estimator();
				estimator.init(option);
				estimator.estimate();
			}
			else if (option.inf){
				Inferencer inferencer = new Inferencer();
				inferencer.init(option);
				
				Model newModel = inferencer.inference();
			
				for (int i = 0; i < newModel.phi.length; ++i){
					//phi: K * V
					System.out.println("-----------------------\ntopic" + i  + " : ");
					for (int j = 0; j < 10; ++j){
						System.out.println(inferencer.globalDict.id2word.get(j) + "\t" + newModel.phi[i][j]);
					}
				}
			}
		}
		catch (CmdLineException cle){
			System.out.println("Command line error: " + cle.getMessage());
			showHelp(parser);
			return;
		}
		catch (Exception e){
			System.out.println("Error in main: " + e.getMessage());
			e.printStackTrace();
			return;
		}
	}
	
	public static void showHelp(CmdLineParser parser){
		System.out.println("LDA [options ...] [arguments...]");
		parser.printUsage(System.out);
	}
	
}
