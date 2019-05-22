package com.innerwave.edms;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.innerwave.edms.core.File;
import com.innerwave.edms.core.Folder;
import com.innerwave.edms.core.Node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	/**
	 *
	 * @param args
	 */
	public static void main(String[] args) {

		Logger logger = LoggerFactory.getLogger(DemoApplication.class);

		SpringApplication.run(DemoApplication.class, args);

		// 폴더만 따로 목록으로 저장해 두고 임의의 폴더를 선택하여 이후의 노드를 추가한다.
		List<Folder> folders = new ArrayList<Folder>();

		Folder folder = new Folder();
		folder.setId(UUID.randomUUID().toString());
		folder.setName("Name of first Folder.");

		folders.add(folder);

		for (int i = 0; i < 1000; i++) {

			// 새로운 노드를 추가할 임의의 폴더를 선택한다.
			folder = folders.get((int) (Math.random() * 100) % folders.size());

			Node node;
			if (Math.random() > 0.5) {
				node = new Folder();
				folders.add((Folder) node);
			} else {
				node = new File();
			}

			// 추가 속성 options
			node.put("icon", "normal");
			node.put("name", "name in option");
			folder.add(node);
		}

		folder = folders.get(0);

		String jsonString = folder.toJSON();

		logger.debug("BEGIN TO JSON -------------------------");

		try {
			Path file = Paths.get("results/file_tree_" + (new Date().getTime()) + ".json");
			List<String> lines = Arrays.asList(jsonString.split("\n"));
			Files.write(file, lines, Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.debug("END TO JSON -------------------------");

		// .writerWithDefaultPrettyPrinter()
		logger.debug("BEGIN FROM JSON -------------------------");
		Node node = Node.fromJSON(jsonString);
		logger.debug(node.toJSON());
		logger.debug("END FROM JSON -------------------------");
	}

}
