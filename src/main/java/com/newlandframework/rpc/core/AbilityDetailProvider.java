package com.newlandframework.rpc.core;

import java.util.Map;

import com.newlandframework.rpc.netty.MessageRecvExecutor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AbilityDetailProvider implements AbilityDetail {
	private static final String STYLE = "<style type=\"text/css\">\n" + "table.gridtable {\n" + "    font-family: verdana,arial,sans-serif;\n" + "    font-size:11px;\n" + "    color:#333333;\n"
			+ "    border-width: 1px;\n" + "    border-color: #666666;\n" + "    border-collapse: collapse;\n" + "}\n" + "table.gridtable th {\n" + "    border-width: 1px;\n" + "    padding: 8px;\n"
			+ "    border-style: solid;\n" + "    border-color: #666666;\n" + "    background-color: #dedede;\n" + "}\n" + "table.gridtable td {\n" + "    border-width: 1px;\n" + "    padding: 8px;\n"
			+ "    border-style: solid;\n" + "    border-color: #666666;\n" + "    background-color: #ffffff;\n" + "}\n" + "</style>";

	private static final String HEADER = "<table class=\"gridtable\">\n" + "<tr>\n" + "    <th>NettyRPC Ability Detail</th>\n" + "</tr>";

	private static final String TAIL = "</table>";
	private static final String CELL_BEGIN = "<tr><td>";
	private static final String CELL_END = "</td></tr>";

	@Override
	public StringBuilder listAbilityDetail(boolean html) {
		Map<String, Object> map = MessageRecvExecutor.getInstance().getHandlerMap();

		ReflectionUtils utils = new ReflectionUtils();

		if (html) {
			utils.getProvider().append(STYLE).append(HEADER);
		}

		map.keySet().stream().forEach(key -> {
			try {
				if (html) {
					utils.getProvider().append(CELL_BEGIN);
					utils.listRpcProviderDetail(Class.forName(key), html);
					utils.getProvider().append(CELL_END);
				} else {
					utils.listRpcProviderDetail(Class.forName(key), html);
				}

			} catch (ClassNotFoundException e) {
				log.error(e.getMessage(), e);
			}
		});

		if (html) {
			utils.getProvider().append(TAIL);
		}

		return utils.getProvider();
	}
}
