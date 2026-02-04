package com.terransky.p3timerjda.utilities.command;

import com.terransky.p3timerjda.utilities.exceptions.DiscordAPIException;
import com.terransky.p3timerjda.utilities.general.InteractionType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class EventBlob {

    private Guild guild;
    private Member member;
    private MessageChannelUnion channelUnion;
    private InteractionType interactionType;

    /**
     * An object containing checked non-null {@link Guild} object and {@link Member} object.
     *
     * @param guild  A {@link Guild} from an event.
     * @param member A {@link Member} from an event.
     */
    public EventBlob(Guild guild, Member member) {
        this.guild = guild;
        this.member = member;
    }

    public Guild getGuild() {
        return guild;
    }

    public EventBlob setGuild(Guild guild) {
        this.guild = guild;
        return this;
    }

    /**
     * @see Guild#getName()
     */
    public String getGuildName() {
        return guild.getName();
    }

    /**
     * @see Guild#getId()
     */
    public String getGuildId() {
        return guild.getId();
    }

    /**
     * @see Guild#getIdLong()
     */
    public long getGuildIdLong() {
        return guild.getIdLong();
    }

    public Member getSelfMember() {
        return guild.getSelfMember();
    }

    public Member getMember() {
        return member;
    }

    public EventBlob setMember(Member member) {
        this.member = member;
        return this;
    }

    /**
     * @see Member#getEffectiveAvatarUrl()
     */
    public String getMemberEffectiveAvatarUrl() {
        return member.getEffectiveAvatarUrl();
    }

    /**
     * @see net.dv8tion.jda.api.entities.User#getName()
     */
    public String getMemberName() {
        return member.getUser().getName();
    }

    /**
     * @see Member#getId()
     */
    public String getMemberId() {
        return member.getId();
    }

    /**
     * @see Member#getIdLong()
     */
    public long getMemberIdLong() {
        return member.getIdLong();
    }

    /**
     * Get a list of all members in a guild.
     *
     * @return A list of members
     */
    public List<Member> getMembers() {
        if (guild.getMembers().isEmpty())
            return new ArrayList<>() {{
                guild.loadMembers()
                    .onSuccess(this::addAll)
                    .onError(DiscordAPIException::new);
            }};
        return guild.getMembers();
    }

    /**
     * Get a filtered list of all members in a guild.
     *
     * @param predicate a predicate to apply to each element to determine if it should be included
     * @return A list of members
     */
    public List<Member> getMembers(Predicate<? super Member> predicate) {
        return getMembers().stream().filter(predicate).toList();
    }

    /**
     * Get a list of all members in a guild that are not a bot.
     *
     * @return A list of members
     */
    public List<Member> getNonBotMembers() {
        return getMembers(EventBlob.this::isNotBot);
    }

    /**
     * Get a list of all cached members in a guild that are not a bot and some other filter.
     *
     * @param predicate a predicate to apply to each element to determine if it should be included
     * @return A list of members
     */
    public List<Member> getNonBotMembers(Predicate<? super Member> predicate) {
        return getNonBotMembers().stream().filter(predicate).toList();
    }

    /**
     * Get a list of all members that are not bots and the bot of this instance
     *
     * @return A list of members
     */
    public List<Member> getNonBotMembersAndSelf() {
        return getNonBotMembersAndSelf(member -> true);
    }

    /**
     * Get a filtered list of all members that are not bots and the bot of this instance
     *
     * @param predicate a predicate to apply to each element to determine if it should be included
     * @return A list of members
     */
    public List<Member> getNonBotMembersAndSelf(Predicate<? super Member> predicate) {
        return getMembers(member -> isNotBot(member) || getSelfMember().equals(member))
            .stream().filter(predicate).toList();
    }

    /**
     * Checks if the member is a bot.
     *
     * @param member A member to check.
     * @return True if the member is not a bot.
     */
    @Contract("null -> false")
    private boolean isNotBot(Member member) {
        if (member == null)
            return false;
        return !member.getUser().isBot();
    }

    public InteractionType getInteractionType() {
        return interactionType;
    }

    public EventBlob setInteractionType(InteractionType interactionType) {
        this.interactionType = interactionType;
        return this;
    }

    public MessageChannelUnion getChannelUnion() {
        return channelUnion;
    }

    public EventBlob setChannelUnion(MessageChannelUnion channelUnion) {
        this.channelUnion = channelUnion;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventBlob eventBlob = (EventBlob) o;
        return getGuild().equals(eventBlob.getGuild()) &&
            getMember().equals(eventBlob.getMember()) &&
            getChannelUnion().equals(eventBlob.getChannelUnion()) &&
            getInteractionType() == eventBlob.getInteractionType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGuild(), getMember(), getChannelUnion(), getInteractionType());
    }

    @Override
    public String toString() {
        return "EventBlob{" +
            "guild=" + guild +
            ", member=" + member +
            ", channelUnion=" + channelUnion +
            ", interactionType=" + interactionType +
            '}';
    }
}
