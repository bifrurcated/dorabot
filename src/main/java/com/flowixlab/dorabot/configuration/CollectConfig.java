package com.flowixlab.dorabot.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowixlab.dorabot.button.Button;
import com.flowixlab.dorabot.commands.SlashCommand;
import com.flowixlab.dorabot.data.IName;
import com.flowixlab.dorabot.data.MenuData;
import com.flowixlab.dorabot.data.Option;
import com.flowixlab.dorabot.data.RoleData;
import com.flowixlab.dorabot.menu.SelectMenu;
import com.flowixlab.dorabot.utils.ResourcesManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class CollectConfig {
    @Bean
    public Map<String, SlashCommand> slashCommandMap(Collection<SlashCommand> commands) {
        return commands.stream()
                .collect(Collectors.toMap(IName::name, Function.identity()));
    }

    @Bean
    public Map<String, Button> buttonMap(Collection<Button> buttons) {
        return buttons.stream()
                .collect(Collectors.toMap(IName::name, Function.identity()));
    }

    @Bean
    public Map<String, SelectMenu> selectMenuMap(Collection<SelectMenu> selectMenus) {
        return selectMenus.stream()
                .collect(Collectors.toMap(IName::name, Function.identity()));
    }

    @Bean
    public Option[] options(ObjectMapper objectMapper) throws IOException {
        var matcher = new PathMatchingResourcePatternResolver();
        var path = "file:" + ResourcesManager.MENUS_DIR + File.separator + "position.json";
        var resource = matcher.getResource(path);
        return objectMapper.readValue(resource.getInputStream(), Option[].class);
    }

    @Bean
    public Map<String, Option> positionOptions(Option[] options) {
        return Arrays.stream(options)
                .collect(Collectors.toMap(option -> option.name().toLowerCase(), Function.identity()));
    }

    @Bean
    public List<MenuData> menusData(ObjectMapper objectMapper) throws IOException {
        var matcher = new PathMatchingResourcePatternResolver();
        List<MenuData> roles = new ArrayList<>();
        String path = "file:" + ResourcesManager.ROLES_DIR + File.separator + "*.json";
        path = path.replace("\\", "/");
        for (var resource : matcher.getResources(path)) {
            roles.add(objectMapper.readValue(resource.getInputStream(), MenuData.class));
        }
        roles.sort(Comparator.comparingInt(MenuData::id));
        return roles;
    }

    @Bean
    public Map<String, Map<String, RoleData>> rolesMap(List<MenuData> menusData) {
        return menusData.stream()
                .collect(Collectors.toMap(k -> String.valueOf(k.id()), v ->
                        Arrays.stream(v.roles()).collect(Collectors.toMap(k -> String.valueOf(k.id()), Function.identity()))
                ));
    }
}
