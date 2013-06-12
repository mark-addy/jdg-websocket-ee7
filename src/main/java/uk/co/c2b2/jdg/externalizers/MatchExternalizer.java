package uk.co.c2b2.jdg.externalizers;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Set;

import org.infinispan.marshall.AdvancedExternalizer;
import org.infinispan.util.Util;

import uk.co.c2b2.jdg.domain.Match;
import uk.co.c2b2.jdg.domain.MatchOdds;

/**
 * <pre>
 * Example Infinispan Advanced Externalizer implementation
 * Enhanced performance and compression over java.io.Serializable
 * @author maddy
 *
 */
public class MatchExternalizer implements AdvancedExternalizer<Match> {

	private static final long serialVersionUID = -7712132518044296826L;

	@Override
	public void writeObject(ObjectOutput output, Match match) throws IOException {
		output.writeUTF(match.getType());
		output.writeUTF(match.getHome());
		output.writeUTF(match.getAway());
		output.writeLong(match.getVersion());
		output.writeDouble(match.getMatchOdds().getHomeWin());
		output.writeDouble(match.getMatchOdds().getDraw());
		output.writeDouble(match.getMatchOdds().getAwayWin());
	}

	@Override
	public Match readObject(ObjectInput input) throws IOException, ClassNotFoundException {
		Match match = new Match(input.readUTF(), input.readUTF(), input.readUTF(), input.readLong(), new MatchOdds(input.readDouble(), input.readDouble(), input.readDouble()));
		return match;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<Class<? extends Match>> getTypeClasses() {
		return Util.<Class<? extends Match>> asSet(Match.class);
	}

	@Override
	public Integer getId() {
		return 70001;
	}

}
